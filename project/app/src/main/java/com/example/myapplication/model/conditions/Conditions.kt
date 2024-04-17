package com.example.myapplication.model.conditions

enum class Conditions(val value: Double) {
    WAVE_HEIGHT_LOWER_BOUNDS(0.5),
    WIND_SPEED_UPPER_BOUNDS(40.0),
    WAVE_PERIOD_LOWER_BOUNDS(6.0)
}