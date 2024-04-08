package com.example.myapplication.data.waveforecast

import com.example.myapplication.model.waveforecast.PointForecast

interface WaveForecastRepository {
    suspend fun getAllRelevantPointForecasts(): List<PointForecast>
}