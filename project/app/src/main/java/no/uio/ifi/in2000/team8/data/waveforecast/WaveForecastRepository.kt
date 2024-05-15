package no.uio.ifi.in2000.team8.data.waveforecast

import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


interface WaveForecastRepository {
    suspend fun getAllWavePeriods(): AllWavePeriods

}

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {

    override suspend fun getAllWavePeriods(): AllWavePeriods {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        return coroutineScope {
            val mappedForecasts = SurfArea.entries.associateWith {
                async {
                    waveForecastDataSource.fetchNearestPointForecast(
                        it.lat,
                        it.lon
                    )
                }
            }
            val mappedForecastsByDay = mappedForecasts.entries.associate { (area, pf) ->
                val pointForecasts = try {
                    pf.await()
                } catch (e: Exception) {
                    emptyList()
                }
                area to pointForecasts.groupBy {forecast ->
                    LocalDateTime.parse(forecast.forecastTime, dateFormatter).dayOfMonth
                }.mapValues { (_, forecasts) ->
                    forecasts.flatMap { listOf(it.totalPeakPeriod, it.totalPeakPeriod, it.totalPeakPeriod) }
                }
            }
            AllWavePeriods(
                mappedForecastsByDay
            )
        }
    }

}



