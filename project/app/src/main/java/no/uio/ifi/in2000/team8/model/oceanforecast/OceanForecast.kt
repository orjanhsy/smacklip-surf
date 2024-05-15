package com.example.myapplication.model.oceanforecast

data class OceanForecast(
    val geometry: GeometryOF,
    val properties: PropertiesOF,
    val type: String
)