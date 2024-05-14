package com.example.myapplication.model.waveforecast


typealias NewPointForecasts = List<NewPointForecast>;

data class NewPointForecast(
    val totalSignificantWaveHeight: Double,
    val expectedMaximumWaveHeight: Double,
    val totalMeanWaveDirection: Long,
    val totalPeakPeriod: Double,
    val source: String,
    val fileSource: String,
    val forecastTime: String,
    val longitude: Double,
    val latitude: Double,
)