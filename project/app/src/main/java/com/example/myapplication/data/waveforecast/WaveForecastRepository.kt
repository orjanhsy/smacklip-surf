package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


interface WaveForecastRepository {
    suspend fun allRelevantWavePeriodsNext3Days(): AllWavePeriods
    suspend fun wavePeriodsNext3DaysForArea(modelName: String, pointId: Int): Map<Int, List<Double?>>

}

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {

    private suspend fun wavePeriods(modelName: String, pointId: Int, time: String): Pair<String?, Double?> {

        val forecast = waveForecastDataSource.fetchPointForecast(modelName, pointId, time)
        return Pair(forecast.forcastDateTime, forecast.tpLocal)
    }

    /*
    .size=60, apiet henter de neste 60 timene, denne returnerer (ca?) 20 pair<dir, tp>
    - altså hver tredje time - for et område.
     */
    override suspend fun wavePeriodsNext3DaysForArea(modelName: String, pointId: Int): Map<Int, List<Double?>> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        return coroutineScope {

            val tps: List<Deferred<Pair<String?, Double?>>> = availableForecastTimes.map { time ->
                async { wavePeriods(modelName, pointId, time) }
            }

            val newTps = tps.map { it.await() }.flatMap { listOf(it, it, it) }.groupBy (
                {LocalDateTime.parse(it.first, dateFormatter).dayOfMonth}, {it.second}
            )
            newTps
        }
    }

    // map[surfarea] -> List<Pair<Direction, period>>  .size=20
    override suspend fun allRelevantWavePeriodsNext3Days(): AllWavePeriods {
        return coroutineScope {
            val relevantForecasts: Map<SurfArea, Deferred<Map< Int, List<Double?>>>> =
                SurfArea.entries.associateWith {
                    async { wavePeriodsNext3DaysForArea(it.modelName, it.pointId) }
                }
            val newRelevantForecasts = relevantForecasts.entries.associate {
                it.key to it.value.await()
            }
            val allWaveForecasts = AllWavePeriods(newRelevantForecasts)
            allWaveForecasts
        }
    }
}



