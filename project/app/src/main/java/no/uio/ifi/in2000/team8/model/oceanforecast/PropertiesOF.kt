package com.example.myapplication.model.oceanforecast

data class PropertiesOF(
    val meta: MetaOF,
    val timeseries: List<TimeserieOF>
)