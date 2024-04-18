package com.example.myapplication.model.conditions

enum class Conditions(val value: Double) {
    WAVE_HEIGHT_LOWER_BOUND(0.5),
    WAVE_HEIGH_UPPER_BOUND(6.0),
    WIND_SPEED_UPPER_BOUND(40.0),
    WAVE_PERIOD_LOWER_BOUND(6.0)
}