package com.example.myapplication.model.waveforecast

import com.example.myapplication.model.surfareas.SurfArea

data class AllWaveForecasts(
    val forecasts: Map<SurfArea, List<Double?>>
)
