package com.example.myapplication.model.oceanforecast

data class OceanForecast(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)