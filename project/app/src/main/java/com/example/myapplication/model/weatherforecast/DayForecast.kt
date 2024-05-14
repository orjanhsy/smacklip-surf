package com.example.myapplication.model.weatherforecast

import java.time.LocalDateTime

data class DayForecast (
    val data: Map<LocalDateTime, DataAtTime> = mapOf()
)