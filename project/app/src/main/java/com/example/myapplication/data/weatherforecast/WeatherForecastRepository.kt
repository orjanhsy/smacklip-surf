package com.example.myapplication.data.weatherforecast

import com.example.myapplication.data.locationforecast.LocationForecastRepository
import com.example.myapplication.data.locationforecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanForecastRepository
import com.example.myapplication.data.oceanforecast.OceanForecastRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.weatherforecast.AllSurfAreasOFLF
import com.example.myapplication.model.weatherforecast.DataAtTime
import com.example.myapplication.model.weatherforecast.DayForecast
import com.example.myapplication.model.weatherforecast.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface WeatherForecastRepository {
    val ofLfForecast: StateFlow<AllSurfAreasOFLF>
    val wavePeriods: StateFlow<AllWavePeriods>
    val areaInFocus: StateFlow<SurfArea?>
    val dayInFocus: StateFlow<Int?>

    fun updateAreaInFocus(surfArea: SurfArea)
    fun updateDayInFocus(day: Int)
    suspend fun loadOFlF()
    suspend fun loadWavePeriods()
}

class WeatherForecastRepositoryImpl(
    // omitting DI as this is the only area these repositories are used
    private val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl(),
    private val oceanForecastRepository: OceanForecastRepository = OceanForecastRepositoryImpl(),
    private val locationForecastRepository: LocationForecastRepository = LocationForecastRepositoryImpl(),
): WeatherForecastRepository {

    private val _ofLfForecast: MutableStateFlow<AllSurfAreasOFLF> = MutableStateFlow(AllSurfAreasOFLF())
    private val _wavePeriods: MutableStateFlow<AllWavePeriods> = MutableStateFlow(AllWavePeriods())
    private val _areaInFocus: MutableStateFlow<SurfArea?> = MutableStateFlow(null)
    private val _dayInFocus: MutableStateFlow<Int?> = MutableStateFlow(null)


    override val ofLfForecast: StateFlow<AllSurfAreasOFLF> = _ofLfForecast.asStateFlow()
    override val wavePeriods: StateFlow<AllWavePeriods> = _wavePeriods.asStateFlow()
    override val areaInFocus: StateFlow<SurfArea?> = _areaInFocus.asStateFlow()
    override val dayInFocus: StateFlow<Int?> = _dayInFocus.asStateFlow()


    override fun updateAreaInFocus(surfArea: SurfArea) {
        _areaInFocus.update {
            surfArea
        }
    }

    override fun updateDayInFocus(day: Int) {
        _dayInFocus.update {
            day
        }
    }

    // loads all ocean forecast and location forecast data for each surfArea into a stateflow
    override suspend fun loadOFlF() {
        coroutineScope {// wrap in coroutine scope to allow for async calls
            _ofLfForecast.update {
                val all7DayForecasts: Map<SurfArea, Deferred<MutableList<DayForecast>>> = SurfArea.entries.associateWith { sa ->
                    async { getOFLFForArea(sa) }
                }
                AllSurfAreasOFLF(
                    next7Days = all7DayForecasts.keys.associateWith {
                        Forecast7DaysOFLF(all7DayForecasts[it]!!.await())
                    }
                )
            }
        }
    }

    // returns a list of all days of forecast, of and lf data combined into same object DataAtTime
    private suspend fun getOFLFForArea(sa: SurfArea): MutableList<DayForecast> {
        val lf = getLFTimeSeries(sa)
        val of = getOFTimeSeries(sa)

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        val allDayForecasts: MutableList<DayForecast> = mutableListOf()

        for (day in lf.keys) {
            val lfAtDay = lf[day]!! // always exists as day is derived from lf.keys
            val ofAtDay = try {of[day]!!} catch(e: NullPointerException) {continue} // skips iteration if there is not data for both lf and of

            val allDataAtDay: MutableMap<LocalDateTime, MutableList<Any>> = mutableMapOf()
            val dayForecast: MutableMap<LocalDateTime, DataAtTime> = mutableMapOf()

            lfAtDay.map {
                val time = LocalDateTime.parse(it.first, dateFormatter)
                allDataAtDay[time] = mutableListOf()
                allDataAtDay[time]!!.let { intervalData ->
                    intervalData.add(it.second.instant.details.wind_speed)
                    intervalData.add(it.second.instant.details.wind_speed_of_gust)
                    intervalData.add(it.second.instant.details.wind_from_direction)
                    intervalData.add(it.second.instant.details.air_temperature)
                    val symbolCode = it.second.next_1_hours?.summary?.symbol_code ?: it.second.next_6_hours.summary.symbol_code
                    intervalData.add(symbolCode)
                }

            }

            ofAtDay.map {(timeStr, data) ->

                // adds a dataAtTime object to dayForecast if both lf and of have data at that time
                val time = LocalDateTime.parse(timeStr, dateFormatter)
                allDataAtDay[time]?.let {intervalData ->
                    intervalData.add(data.instant.details.sea_surface_wave_height)
                    intervalData.add(data.instant.details.sea_surface_wave_from_direction)

                    val dataAtTime = DataAtTime(
                        windSpeed = intervalData[0] as Double,
                        windGust = intervalData[1] as Double,
                        windDir = intervalData[2] as Double,
                        airTemp = intervalData[3] as Double,
                        symbolCode = intervalData[4] as String,
                        waveHeight = intervalData[5] as Double,
                        waveDir = intervalData[6] as Double,
                    )
                    dayForecast.put(time, dataAtTime)
                }
            }
            allDayForecasts.add(
                DayForecast(
                    data = dayForecast
                )
            )
        }
        return allDayForecasts
    }

    // returns ocean forecast time series mapped to day of month
    private suspend fun getOFTimeSeries(surfArea: SurfArea): Map<Int, List<Pair<String, DataOF>>> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return oceanForecastRepository.getTimeSeries(surfArea).groupBy(
            { LocalDateTime.parse(it.first, dateFormatter).dayOfMonth }, { it }
        )
    }

    // returns location forecast time series mapped to day of month
    private suspend fun getLFTimeSeries(surfArea: SurfArea): Map<Int, List<Pair<String, DataLF>>> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return locationForecastRepository.getTimeSeries(surfArea).groupBy(
            { LocalDateTime.parse(it.first, dateFormatter).dayOfMonth }, { it }
        )
    }

    override suspend fun loadWavePeriods() {
        _wavePeriods.update {
            waveForecastRepository.getAllWavePeriods()
        }
    }


}