package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWaveForecasts
import com.example.myapplication.model.waveforecast.PointForecast
import com.example.myapplication.model.waveforecast.PointForecasts
import io.ktor.http.content.NullBody
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.PI
import kotlin.math.acos
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All


interface WaveForecastRepository {
    suspend fun allRelevantWavePeriodsNext3DaysHardCoded(): AllWaveForecasts

}

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {

    private suspend fun wavePeriods(modelName: String, pointId: Int, time: String): Double? {
        val forecast = waveForecastDataSource.fetchPointForecast(modelName, pointId, time)
        return forecast.tpLocal
    }

    /*
    .size=60, apiet henter de neste 60 timene, denne returnerer (ca?) 20 pair<dir, tp>
    - altså hver tredje time - for et område.
     */
    private suspend fun wavePeriodsNext3DaysForArea(modelName: String, pointId: Int): List<Double?> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes

        return coroutineScope {
            val tps: List<Deferred<Double?>> = availableForecastTimes.map { time ->
                async { wavePeriods(modelName, pointId, time) }
            }

            val newTps = tps.map { it.await() }
            newTps
        }
    }

    // map[surfarea] -> List<Pair<Direction, period>>  .size=20
    override suspend fun allRelevantWavePeriodsNext3DaysHardCoded(): AllWaveForecasts {
        return coroutineScope {
            val relevantForecasts: Map<SurfArea, Deferred<List<Double?>>> =
                SurfArea.entries.associateWith {
                    async { wavePeriodsNext3DaysForArea(it.modelName, it.pointId) }
                }
            val newRelevantForecasts = relevantForecasts.entries.associate {
                it.key to it.value.await()
            }
            val allWaveForecasts = AllWaveForecasts(newRelevantForecasts)
            allWaveForecasts
        }
    }
}



