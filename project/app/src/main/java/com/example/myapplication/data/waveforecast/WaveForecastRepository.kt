package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast

interface WaveForecastRepository {
    suspend fun next3Days(): Map<SurfArea, List<PointForecast>>
}

class WaveForecastRepositoryImpl(
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
): WaveForecastRepository {

    override suspend fun next3Days(): Map<SurfArea, List<PointForecast>> {
        val avaliableForecastTimes = waveForecastDataSource.fetchAvaliableTimestamps().availableForecastTimes

    }
}



