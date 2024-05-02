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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

interface Repository {
    val ofLfNext7Days: StateFlow<AllSurfAreasOFLF>
    val wavePeriods: StateFlow<AllWavePeriods>
    val alerts: StateFlow<Map<SurfArea, List<Alert>>>

    suspend fun loadOFlF()
    suspend fun loadWavePeriods()
    suspend fun loadAlerts()
}

class RepositoryImpl(
    val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl(),
    val oceanForecastRepository: OceanforecastRepository = OceanforecastRepositoryImpl(),
    val locationForecastRepository: LocationForecastRepository = LocationForecastRepositoryImpl(),
    val metAlertsRepository: MetAlertsRepository = MetAlertsRepositoryImpl()

): Repository {


    private val _ofLfNext7Days: MutableStateFlow<AllSurfAreasOFLF> = MutableStateFlow(AllSurfAreasOFLF())
    private val _wavePeriods: MutableStateFlow<AllWavePeriods> = MutableStateFlow(AllWavePeriods())
    private val _alerts: MutableStateFlow<Map<SurfArea, List<Alert>>> = MutableStateFlow(mapOf())

    override val ofLfNext7Days: StateFlow<AllSurfAreasOFLF> = _ofLfNext7Days.asStateFlow()
    override val wavePeriods: StateFlow<AllWavePeriods> = _wavePeriods.asStateFlow()
    override val alerts: StateFlow<Map<SurfArea, List<Alert>>> = _alerts.asStateFlow()

    override suspend fun loadOFlF() {
        _ofLfNext7Days.update {
            //timeSeries
            val of: Map<SurfArea, List<Pair<String, DataOF>>> = getOF()
            val lf: Map<SurfArea, List<Pair<String, DataLF>>> = getLF()

            val forecasts: Map<SurfArea, Forecast7DaysOFLF> = SurfArea.entries.associateWith {sa ->
                val data: MutableMap<LocalDateTime, MutableList<Any>> = mutableMapOf()
                val dataPerDay: MutableMap<Int, MutableMap<LocalDateTime, DataAtTime>> = mutableMapOf()

                lf[sa]!!.map {
                    val time = LocalDateTime.parse(it.first)
                    val windSpeed = it.second.instant.details.wind_speed
                    val windGust = it.second.instant.details.wind_speed_of_gust
                    val windDir = it.second.instant.details.wind_from_direction
                    val airTemp = it.second.instant.details.air_temperature
                    val symbolCode = try {
                        it.second.next_1_hours.summary.symbol_code
                    } catch (e: NullPointerException){
                        it.second.next_6_hours.summary.symbol_code
                    }
                    data[time] = mutableListOf(windSpeed, windGust, windDir, airTemp, symbolCode)
                }

                of[sa]!!.map{
                    val time = LocalDateTime.parse(it.first)
                    val waveHeight = it.second.instant.details.sea_surface_wave_height
                    val waveDir = it.second.instant.details.sea_water_to_direction

                    try {
                        if (data[time]!!.size == 7) {
                            /*
                            Filtrerer ut steder hvor man ikke har all nødvendig data.
                            Dette haandterer steder hvor lf har storre intervaller mellom data enn of.
                             */

                            data[time]!!.add(waveHeight)
                            data[time]!!.add(waveDir)

                            val dataAtTime = DataAtTime(
                                windSpeed   = data[time]!![0] as Double,
                                windGust    = data[time]!![1] as Double,
                                windDir     = data[time]!![2] as Double,
                                airTemp     = data[time]!![3] as Double,
                                symbolCode  = data[time]!![4] as String,
                                waveHeight  = data[time]!![5] as Double,
                                waveDir     = data[time]!![6] as Double,
                            )

                            // created a map of day -> map<time, data>
                            try { dataPerDay[time.dayOfMonth]!!.put(time, dataAtTime)}
                            catch (e: NullPointerException) {
                                dataPerDay[time.dayOfMonth] = mutableMapOf(time to dataAtTime)
                            }
                        } else {}
                    }catch (_: Exception) {
                        Log.d("REPO", "No LF data at $time")
                    }
                }

                // maps dataPerDay values to DayForecast objects
                val dayForecasts: List<DayForecast> = dataPerDay.map {
                    DayForecast(
                        data = it.value.toMap()
                    )
                }

                val forecast7DaysOfLF = Forecast7DaysOFLF(
                    forecast = dayForecasts
                )

                // associates a surfArea with its 7 day forecast
                forecast7DaysOfLF
            }

            val oflf = AllSurfAreasOFLF(
                next7Days = forecasts
            )

            oflf
        }

    }

    private suspend fun getOF(): Map<SurfArea, List<Pair<String, DataOF>>> {
        return SurfArea.entries.associateWith {
            oceanForecastRepository.getTimeSeries(it)
        }
    }

    private suspend fun getLF(): Map<SurfArea, List<Pair<String, DataLF>>> {
        return SurfArea.entries.associateWith {
            locationForecastRepository.getTimeSeries(it)
        }
    }

    override suspend fun loadWavePeriods() {
        TODO("Not yet implemented")
    }

    override suspend fun loadAlerts() {
        TODO("Not yet implemented")
    }


}