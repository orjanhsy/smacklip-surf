package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast
import com.example.myapplication.model.waveforecast.PointForecasts
import io.ktor.http.content.NullBody
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.PI


interface WaveForecastRepository {
    suspend fun allRelevantWavePeriodAndDirNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>>
    suspend fun retrieveRelevantModelNamesAndPointIds(): Map<SurfArea, Pair<String?, Int?>> // for tests

    suspend fun waveDirAndPeriodNext3DaysForArea(modelName: String, pointId: Int): List<Pair<Double?, Double?>>
    suspend fun allRelevantWavePeriodAndDirNext3DaysHardCoded(): Map<SurfArea, List<Pair<Double?, Double?>>>
    fun distanceTo(lat: Double, lon: Double, surfArea: SurfArea): Double // for tests
    suspend fun pointForecast(modelName: String, pointId: Int, time: String): PointForecast // for tests
    }

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {


    //hardkodet: Surfarea.modelName (område), SurfArea.pointId(punkt)
    override suspend fun pointForecast(modelName: String, pointId: Int, time: String): PointForecast {
        return waveForecastDataSource.fetchPointForecast(modelName, pointId, time)
    }

    //Pair<direction, wavePeriod>
    private suspend fun waveDirAndPeriod(modelName: String, pointId: Int, time: String): Pair<Double?, Double?> {
        val forecast = pointForecast(modelName, pointId, time)
        return Pair(forecast.dirLocal, forecast.tpLocal)
    }

    /*
    .size=60, apiet henter de neste 60 timene, denne returnerer (ca?) 20 pair<dir, tp>
    - altså hver tredje time - for et område.
     */
    override suspend fun waveDirAndPeriodNext3DaysForArea(modelName: String, pointId: Int): List<Pair<Double?, Double?>> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes
        val dirAndTp: List<Pair<Double?, Double?>> = availableForecastTimes.map {time ->
            waveDirAndPeriod(modelName, pointId, time)
        }
        return dirAndTp
    }

    // map[surfarea] -> List<Pair<Direction, period>>  .size=20
    override suspend fun allRelevantWavePeriodAndDirNext3DaysHardCoded(): Map<SurfArea, List<Pair<Double?, Double?>>> {
        val relevantForecasts: Map<SurfArea, List<Pair<Double?, Double?>>> = SurfArea.entries.associateWith {sa ->
            waveDirAndPeriodNext3DaysForArea(sa.modelName, sa.pointId)
        }
        return relevantForecasts
    }

    override suspend fun allRelevantWavePeriodAndDirNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>> {
        val modelNamesAndPointIds: Map<SurfArea, Pair<String?, Int?>> = retrieveRelevantModelNamesAndPointIds()
        val relevantForecasts: Map<SurfArea, List<Pair<Double?, Double?>>> = SurfArea.entries.associateWith {sa ->
            val modelName = modelNamesAndPointIds[sa]?.first!!
            val pointId = modelNamesAndPointIds[sa]?.second!!

            waveDirAndPeriodNext3DaysForArea(modelName, pointId)
        }
        return relevantForecasts
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
        // acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon2-lon1))*6371
        val radius = 6371.0 // Earth radius in kilometers
        val lat1Rad = lat * PI / 180.0
        val lon1Rad = lon * PI / 180.0
        val lat2Rad = surfArea.lat * PI / 180.0
        val lon2Rad = surfArea.lon * PI / 180.0
        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad
        val a = sin(dLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c
    }


}



