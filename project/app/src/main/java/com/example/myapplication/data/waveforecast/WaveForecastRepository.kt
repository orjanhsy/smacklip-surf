package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast

interface WaveForecastRepository {
    suspend fun pointForecastNext3Days(): Map<String, List<List<PointForecast>>>
}

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {

    private fun inArea(lat: Double?, lon: Double?, surfArea: SurfArea, radius: Double = 0.1): Boolean {
        return (
                lat!! in surfArea.lat - radius..surfArea.lat + radius &&
                lon!! in surfArea.lon - radius..surfArea.lon + radius
                )
    }

    /*
    TODO:
    Make it faster. Unsure how to do that without hardcoding
    - Might not have to retrieve all the data in one single call
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
}



