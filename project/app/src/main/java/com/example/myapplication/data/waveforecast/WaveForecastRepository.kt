package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast
import io.ktor.http.content.NullBody
import kotlin.math.abs

interface WaveForecastRepository {
    suspend fun pointForecastNext3Days(): Map<String, List<List<PointForecast>>>
    suspend fun allRelevantWavePeriodAndDirNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>>
    suspend fun retrieveRelevantModelNamesAndPointIds(): Map<SurfArea, Pair<String?, Int?>>

    }

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {

    private fun inArea(lat: Double?, lon: Double?, surfArea: SurfArea, radius: Double = 0.5): Boolean {
        return (
            lat!! in surfArea.lat - radius..surfArea.lat + radius &&
            lon!! in surfArea.lon - radius..surfArea.lon + radius
        )
    }

    /*
    TODO:
    Make it call on pointforecast for each surfarea instead of filtering from fetchAllPointForecasts and measure time spent.
    */
    override suspend fun pointForecastNext3Days(): Map<String, List<List<PointForecast>>> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes

        val allPointForecastsNext3Days = mutableMapOf<String, List<List<PointForecast>>>()
        SurfArea.entries.map{
            allPointForecastsNext3Days.put(
                it.locationName,
                availableForecastTimes.map {time ->
                    waveForecastDataSource.fetchAllPointForecasts(time).filter { pointForecast ->
                        inArea(pointForecast.lat, pointForecast.lon, it)
                    }.sortedBy { pointForecast -> pointForecast.forcastDateTime}
                }
            )
        }

        return allPointForecastsNext3Days
    }


    //hardkodet: Surfarea.modelName (område), SurfArea.pointId(punkt)
    private suspend fun pointForecast(modelName: String, pointId: Int, time: String): PointForecast {

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
    suspend fun waveDirAndPeriodNext3DaysForArea(modelName: String, pointId: Int): List<Pair<Double?, Double?>> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes
        val dirAndTp: List<Pair<Double?, Double?>> = availableForecastTimes.map {time ->
            waveDirAndPeriod(modelName, pointId, time)
        }
        return dirAndTp
    }

    // map[surfarea] -> List<Pair<Direction, period>>  .size=20
    override suspend fun allRelevantWavePeriodAndDirNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>> {
        val relevantForecasts: Map<SurfArea, List<Pair<Double?, Double?>>> = SurfArea.entries.associateWith {sa ->
            waveDirAndPeriodNext3DaysForArea(sa.modelName, sa.pointId)
        }
        return relevantForecasts
    }

    // Map( SurfArea -> (modelName, pointId)
    override suspend fun retrieveRelevantModelNamesAndPointIds(): Map<SurfArea, Pair<String?, Int?>> {
        val time = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes[1]
        val allForecasts = waveForecastDataSource.fetchAllPointForecasts(time)
        val modelNamesAndIds = SurfArea.entries.associateWith {area ->
            var closest: Pair<Double, PointForecast?> = Pair(100.0, null)
            allForecasts.forEach { pointForecast ->
                val distanceToPoint = distanceTo(pointForecast.lat, pointForecast.lon, area)
                if(distanceToPoint < closest.first) {
                    closest = Pair(distanceToPoint, pointForecast)
                }
            }
            Pair(closest.second?.modelName, closest.second?.idNumber)
        }
        return modelNamesAndIds
    }

    private fun distanceTo(lat: Double, lon: Double, surfArea: SurfArea): Double {
        val areaLat = surfArea.lat
        val areaLon = surfArea.lon

        return abs(areaLat-lat + areaLon-lon)
    }
}



