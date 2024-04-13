package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast
import io.ktor.http.content.NullBody
import kotlin.math.abs

interface WaveForecastRepository {
    suspend fun pointForecastNext3Days(): Map<String, List<List<PointForecast>>>
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
    private suspend fun pointForecast(surfArea: SurfArea, time: String): PointForecast {

        return waveForecastDataSource.fetchPointForecast(surfArea.modelName, surfArea.pointId, time)
    }

    //Pair<direction, wavePeriod>
    private suspend fun waveDirAndPeriod(surfArea: SurfArea, time: String): Pair<Double?, Double?> {
        val forecast = pointForecast(surfArea, time)
        return Pair(forecast.dirLocal, forecast.tpLocal)
    }

    /*
    .size=60, apiet henter de neste 60 timene, denne returnerer (ca?) 20 pair<dir, tp>
    - altså hver tredje time - for et område.
     */
    suspend fun waveDirAndPeriodNext3DaysForArea(surfArea: SurfArea): List<Pair<Double?, Double?>> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes
        val dirAndTp: List<Pair<Double?, Double?>> = availableForecastTimes.map {time ->
            waveDirAndPeriod(surfArea, time)
        }
        assert(dirAndTp.size == 20) {"Size should be 20, was ${dirAndTp.size}"}
        return dirAndTp
    }

    // map[surfarea] -> List<Pair<Direction, period>>  .size=20
    suspend fun allRelevantWavePeriodAndDirNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>> {
        val allForecasts = SurfArea.entries.associateWith{ area ->
            waveDirAndPeriodNext3DaysForArea(area)
        }
        return allForecasts
    }

    private suspend fun retrieveRelevantModelNamesAndPointIds(): Map<SurfArea, Pair<String?, Double?>> {
        val time = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes[0]
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



