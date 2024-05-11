package com.example.myapplication.data.waveforecast

import android.util.Log
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import com.example.myapplication.model.waveforecast.NewPointForecasts
import com.example.myapplication.model.waveforecast.PointForecast
import com.example.myapplication.model.waveforecast.PointForecasts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All


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
                    waveForecastDataSource.fetchPointForecastWithTimeTimestamps(
                        it.lat,
                        it.lon
                    )
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



