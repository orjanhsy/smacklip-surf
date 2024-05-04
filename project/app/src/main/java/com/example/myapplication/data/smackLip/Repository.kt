package com.example.myapplication.data.smackLip

import android.util.Log
import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsRepository
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.smacklip.DayForecast
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

interface Repository {
    val ofLfNext7Days: StateFlow<AllSurfAreasOFLF>
    val wavePeriods: StateFlow<AllWavePeriods>
    val alerts: StateFlow<Map<SurfArea, List<Alert>>>
    val areaInFocus: StateFlow<SurfArea?>
    val dayInFocus: StateFlow<Int?>

    fun updateAreaInFocus(surfArea: SurfArea)
    fun updateDayInFocus(day: Int)
    suspend fun loadOFlF()
    suspend fun loadWavePeriods()
    suspend fun loadAlerts()
}

class RepositoryImpl(
    private val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl(),
    private val oceanForecastRepository: OceanforecastRepository = OceanforecastRepositoryImpl(),
    private val locationForecastRepository: LocationForecastRepository = LocationForecastRepositoryImpl(),
    private val metAlertsRepository: MetAlertsRepository = MetAlertsRepositoryImpl()

): Repository {

    private val _ofLfNext7Days: MutableStateFlow<AllSurfAreasOFLF> = MutableStateFlow(AllSurfAreasOFLF())
    private val _wavePeriods: MutableStateFlow<AllWavePeriods> = MutableStateFlow(AllWavePeriods())
    private val _alerts: MutableStateFlow<Map<SurfArea, List<Alert>>> = MutableStateFlow(mapOf())
    private val _areaInFocus: MutableStateFlow<SurfArea?> = MutableStateFlow(null)
    private val _dayInFocus: MutableStateFlow<Int?> = MutableStateFlow(null)


    override val ofLfNext7Days: StateFlow<AllSurfAreasOFLF> = _ofLfNext7Days.asStateFlow()
    override val wavePeriods: StateFlow<AllWavePeriods> = _wavePeriods.asStateFlow()
    override val alerts: StateFlow<Map<SurfArea, List<Alert>>> = _alerts.asStateFlow()
    override val areaInFocus: StateFlow<SurfArea?> = _areaInFocus.asStateFlow()
    override val dayInFocus: StateFlow<Int?> = _dayInFocus.asStateFlow()


    init {
        CoroutineScope(Dispatchers.IO).launch{
            loadOFlF()
            loadAlerts()
            loadWavePeriods()
        }
    }


    override fun updateAreaInFocus(surfArea: SurfArea) {
        Log.d("REPO", "Updating areaInFocus to $surfArea")
        _areaInFocus.update {
            surfArea
        }
    }

    override fun updateDayInFocus(day: Int) {
        Log.d("REPO", "Updating dayInFocus to $day")
        _dayInFocus.update {
            day
        }
    }

    /*
            Funksjonen henter all oflf data for hvert sted, som resulterer i en 9-dagers forecast (den tar med dager der windGust er 0.0  forelopig)
             */
    override suspend fun loadOFlF() {
        Log.d("REPO", "Updating OFLF")
        withContext(Dispatchers.IO) {// burde context bli sendt ned fra vm?
            _ofLfNext7Days.update {
                val all7DayForecasts: Map<SurfArea, Deferred<MutableList<DayForecast>>> = SurfArea.entries.associateWith { sa ->
                    async { getOFLFForArea(sa) }
                }
                Log.d("REPO", "Loading oflf is complete")
                AllSurfAreasOFLF(
                    next7Days = all7DayForecasts.keys.associateWith {
                        Forecast7DaysOFLF(all7DayForecasts[it]!!.await())
                    }
                )
            }
        }
    }

    private suspend fun getOFLFForArea(sa: SurfArea): MutableList<DayForecast> {
        val lf = getLFTimeSeries(sa)
        val of = getOFTimeSeries(sa)

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        val allDayForecasts: MutableList<DayForecast> = mutableListOf()

        for (day in lf.keys) {
            val dayForecasts: MutableMap<LocalDateTime, DataAtTime> = mutableMapOf()
            // TODO: !!
            val lfAtDay = lf[day]!!
            val ofAtDay = try {of[day]!!} catch(e: NullPointerException) {continue} // skips iteration if there is not data for both lf and of

            val allDataAtDay: MutableMap<LocalDateTime, MutableList<Any>> = mutableMapOf()
            val dayForecast: MutableMap<LocalDateTime, DataAtTime> = mutableMapOf()

            lfAtDay.map {
                val time = LocalDateTime.parse(it.first, dateFormatter)
                allDataAtDay[time] = mutableListOf()
                allDataAtDay[time]!!.add(it.second.instant.details.wind_speed)
                allDataAtDay[time]!!.add(it.second.instant.details.wind_speed_of_gust)
                allDataAtDay[time]!!.add(it.second.instant.details.wind_from_direction)
                allDataAtDay[time]!!.add(it.second.instant.details.air_temperature)
                val symbolCode = try {
                    it.second.next_1_hours.summary.symbol_code
                } catch (e: NullPointerException) {
                    // for days where there are no longer hourly forecasts
                    it.second.next_6_hours.summary.symbol_code
                }
                allDataAtDay[time]!!.add(symbolCode)
            }

            ofAtDay.map {
                val time = LocalDateTime.parse(it.first, dateFormatter)
                try {
                    allDataAtDay[time]!!.add(it.second.instant.details.sea_surface_wave_height)
                    allDataAtDay[time]!!.add(it.second.instant.details.sea_water_to_direction)

                    val dataAtTime = DataAtTime(
                        windSpeed   = allDataAtDay[time]!![0] as Double,
                        windGust    = allDataAtDay[time]!![1] as Double,
                        windDir     = allDataAtDay[time]!![2] as Double,
                        airTemp     = allDataAtDay[time]!![3] as Double,
                        symbolCode  = allDataAtDay[time]!![4] as String,
                        waveHeight  = allDataAtDay[time]!![5] as Double,
                        waveDir     = allDataAtDay[time]!![6] as Double,
                    )
                    dayForecast.put(time, dataAtTime)
                } catch(_: NullPointerException) {
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

    private suspend fun getOFTimeSeries(surfArea: SurfArea): Map<Int, List<Pair<String, DataOF>>> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return oceanForecastRepository.getTimeSeries(surfArea).groupBy(
            { LocalDateTime.parse(it.first, dateFormatter).dayOfMonth }, { it }
        )
    }

    private suspend fun getLFTimeSeries(surfArea: SurfArea): Map<Int, List<Pair<String, DataLF>>> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return locationForecastRepository.getTimeSeries(surfArea).groupBy(
            { LocalDateTime.parse(it.first, dateFormatter).dayOfMonth }, { it }
        )
    }

    override suspend fun loadWavePeriods() {
        Log.d("REPO", "Updating waveperiods")

        _wavePeriods.update {
            waveForecastRepository.allRelevantWavePeriodsNext3Days()
        }
    }

    override suspend fun loadAlerts() {
        Log.d("REPO", "Updating Alerts")

        _alerts.update {
            metAlertsRepository.getAllRelevantAlerts()
        }
    }


}