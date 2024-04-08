package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast

interface WaveForecastRepository {
    suspend fun next3Days(): List<Pair<String, List<PointForecast>>>
}

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {

    private fun inArea(lat: Double?, lon: Double?, surfArea: SurfArea, radius: Double = 0.7): Boolean {
        return (
                lat!! in surfArea.lat - radius..surfArea.lat + radius &&
                lon!! in surfArea.lon - radius..surfArea.lon + radius
                )
    }
    override suspend fun next3Days(): List<Pair<String, List<PointForecast>>> {
        val availableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes
        val allPointForecastsNext3Days = availableForecastTimes.map {
            it to waveForecastDataSource.fetchAllPointForecasts(it).filter { pointForecast ->
                inArea(pointForecast.lat, pointForecast.lon, SurfArea.HODDEVIK)
            }
        }

        return allPointForecastsNext3Days
    }
}



