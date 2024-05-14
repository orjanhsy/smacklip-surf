package com.example.myapplication.model.waveforecast

import com.example.myapplication.model.surfareas.SurfArea

data class AllWavePeriods(
    val wavePeriods: Map<SurfArea, Map<Int, List<Double?>>> = mapOf()
)
