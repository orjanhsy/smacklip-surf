package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
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
    suspend fun allRelevantWavePeriodsNext3Days(): AllWavePeriods
    suspend fun wavePeriodsNext3DaysForArea(modelName: String, pointId: Int): Map<Int, List<Double?>>

    suspend fun allPointForecasts(): Map<LocalDateTime, List<PointForecast>>
    suspend fun allWavePeriodsNext3Days(): AllWavePeriods

}

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {
    override suspend fun allWavePeriodsNext3Days(): AllWavePeriods {
        val pointForecasts: Map<LocalDateTime, List<PointForecast>> = allPointForecasts()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val wavePeriods: Map<SurfArea, Map<Int, List<Double?>>> = SurfArea.entries.associateWith {sa ->
            pointForecasts.map {
                val pointData = it.value.filter {point -> point.modelName == sa.modelName && point.idNumber == sa.pointId }[0]
                val day = LocalDateTime.parse(pointData.forcastDateTime, dateFormatter).dayOfMonth
                Pair(day, pointData.tpLocal)
            }.groupBy (
                {it.first}, {it.second}
            )
        }

        val hourByHour = SurfArea.entries.associateWith { sa ->
            wavePeriods[sa]!!.entries.associate { day ->
                day.key to day.value.flatMap { listOf(it, it, it) }
            }
        }

        return AllWavePeriods(hourByHour)
    }

    private fun distanceTo(lat: Double, lon: Double, surfArea: SurfArea): Double {
        // acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon2-lon1))*6371 (6371 is Earth radius in km.)
        val radiusEarth = 6371
        val lat1 = surfArea.lat * PI / 180
        val lon1 = surfArea.lon * PI / 180
        val lat2 = lat * PI / 180
        val lon2 = lon * PI / 180
        return acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2-lon1)) * radiusEarth
    }

    private suspend fun wavePeriods(modelName: String, pointId: Int, time: String): Pair<String?, Double?> {

        val forecast = waveForecastDataSource.fetchPointForecast(modelName, pointId, time)
        return Pair(forecast.forcastDateTime, forecast.tpLocal)
    }

    override suspend fun allPointForecasts(): Map<LocalDateTime, List<PointForecast>> {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val availableTimeStamps = waveForecastDataSource.fetchAvaliableTimestamps()
        val allPointForecasts = coroutineScope {
            availableTimeStamps.availableForecastTimes.associate {
                LocalDateTime.parse(it, dateFormatter) to
                async { waveForecastDataSource.fetchAllPointForecasts(it) }
            }
        }
        return allPointForecasts.entries.associate {
            it.key to it.value.await()
        }
    }



    /*
        .size=60, apiet henter de neste 60 timene, denne returnerer (ca?) 20 pair<dir, tp>
        - altså hver tredje time - for et område.
         */
    override suspend fun wavePeriodsNext3DaysForArea(modelName: String, pointId: Int): Map<Int, List<Double?>> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

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



