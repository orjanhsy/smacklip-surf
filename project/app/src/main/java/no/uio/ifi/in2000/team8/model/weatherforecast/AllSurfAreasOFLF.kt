package com.example.myapplication.model.weatherforecast

import com.example.myapplication.model.surfareas.SurfArea

data class AllSurfAreasOFLF (
    val next7Days: Map<SurfArea, Forecast7DaysOFLF> = mapOf()
)