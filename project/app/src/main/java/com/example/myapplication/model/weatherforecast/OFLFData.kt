package com.example.myapplication.model.weatherforecast
import com.example.myapplication.model.surfareas.SurfArea
import java.time.LocalDateTime

data class AllSurfAreasOFLF (
    val next7Days: Map<SurfArea, Forecast7DaysOFLF> = mapOf()
)

data class Forecast7DaysOFLF(
    val forecast: List<DayForecast> = listOf()
)
data class DayForecast (
    val data: Map<LocalDateTime, DataAtTime> = mapOf()
)


data class DataAtTime (
    // [windSpeed, windSpeedOfGust, windDirection, airTemperature, symbolCode, Waveheight, waveDirection]
    val windSpeed: Double,
    val windGust: Double,
    val windDir: Double,
    val airTemp: Double,
    val symbolCode: String,
    val waveHeight: Double,
    val waveDir: Double
)