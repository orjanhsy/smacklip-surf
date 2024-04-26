package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
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


interface WaveForecastRepository {
    suspend fun retrieveRelevantModelNamesAndPointIds(): Map<SurfArea, Pair<String?, Int?>> // for tests

    suspend fun wavePeriodsNext3DaysForArea(modelName: String, pointId: Int): List<Double?>
    suspend fun allRelevantWavePeriodsNext3DaysHardCoded(): Map<SurfArea, List<Double?>>

    suspend fun wavePeriods(modelName: String, pointId: Int, time: String): Double? // for tests
    suspend fun pointForecast(modelName: String, pointId: Int, time: String): PointForecast // for tests
    fun distanceTo(lat: Double, lon: Double, surfArea: SurfArea): Double // for tests
    }

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {



    override suspend fun pointForecast(modelName: String, pointId: Int, time: String): PointForecast {
        return waveForecastDataSource.fetchPointForecast(modelName, pointId, time)
    }

    override suspend fun wavePeriods(modelName: String, pointId: Int, time: String): Double? {
        val forecast = waveForecastDataSource.fetchPointForecast(modelName, pointId, time)
        return forecast.tpLocal
    }

    /*
    .size=60, apiet henter de neste 60 timene, denne returnerer (ca?) 20 pair<dir, tp>
    - altså hver tredje time - for et område.
     */
    override suspend fun wavePeriodsNext3DaysForArea(modelName: String, pointId: Int): List<Double?> {
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
    override suspend fun allRelevantWavePeriodsNext3DaysHardCoded(): Map<SurfArea, List<Double?>> {
        return coroutineScope {
            val relevantForecasts: Map<SurfArea, Deferred<List<Double?>>> =
                SurfArea.entries.associateWith {
                    async { wavePeriodsNext3DaysForArea(it.modelName, it.pointId) }
                }
            val newRelevantForecasts = relevantForecasts.entries.associate {
                it.key to it.value.await()
            }

            newRelevantForecasts
        }
    }


    // Map( SurfArea -> (modelName, pointId)
    override suspend fun retrieveRelevantModelNamesAndPointIds(): Map<SurfArea, Pair<String?, Int?>> {
        val time = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes[1]
        val allForecasts = waveForecastDataSource.fetchAllPointForecasts(time)
        val modelNamesAndIds = SurfArea.entries.associateWith {area ->
            getClosestPointForecast(allForecasts, area)
        }
        return modelNamesAndIds
    }

    private fun getClosestPointForecast(allForecasts: List<PointForecast>, surfArea: SurfArea): Pair<String?, Int?> {
        var closest: Pair<Double, PointForecast?> = Pair(100.0, null)
        allForecasts.forEach { pointForecast ->
            val distanceToPoint = distanceTo(pointForecast.lat, pointForecast.lon, surfArea)
            if(distanceToPoint < closest.first) {
                closest = Pair(distanceToPoint, pointForecast)
            }
        }
        return Pair(closest.second?.modelName, closest.second?.idNumber)
    }

    override fun distanceTo(lat: Double, lon: Double, surfArea: SurfArea): Double {
        // acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon2-lon1))*6371 (6371 is Earth radius in km.)
        val radiusEarth = 6371
        val lat1 = surfArea.lat * PI / 180
        val lon1 = surfArea.lon * PI / 180
        val lat2 = lat * PI / 180
        val lon2 = lon * PI / 180
        return acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2-lon1)) * radiusEarth
    }
}



