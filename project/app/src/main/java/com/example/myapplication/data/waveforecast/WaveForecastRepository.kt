package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
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
                    try {
                        waveForecastDataSource.fetchNearestPointForecast(
                            it.lat,
                            it.lon
                        )
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }
            val mappedForecastsByDay = mappedForecasts.entries.associate { (area, pointForecasts) ->
                area to pointForecasts.await().groupBy {forecast ->
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



