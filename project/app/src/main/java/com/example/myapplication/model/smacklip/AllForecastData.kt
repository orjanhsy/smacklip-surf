package com.example.myapplication.model.smacklip
import com.example.myapplication.model.surfareas.SurfArea

data class AllSurfAreas (
    val next7Days: Map<SurfArea, List<DayData>> = mapOf()
)

data class DayData (
    val dayData: Map<List<Int>, DataAtTime>
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