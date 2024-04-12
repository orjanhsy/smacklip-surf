package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast

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

    //hardkodet: Surfarea.
    suspend fun areaPointForecastNext3Days(surfArea: SurfArea, time: String)  {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes

        val forecast = waveForecastDataSource.fetchPointForecast(surfArea.modelName, surfArea.pointId, time)

    }
}



