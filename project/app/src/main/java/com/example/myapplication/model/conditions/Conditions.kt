package com.example.myapplication.model.conditions

enum class Conditions(val value: Double) {
    // always poor if:
    WAVE_HEIGHT_LOWER_BOUND(0.5),
    WAVE_HEIGHT_UPPER_BOUND(8.0),
    WIND_SPEED_UPPER_BOUND(15.0),
    WAVE_PERIOD_LOWER_BOUND(6.0),


    WIND_SPEED_GREAT_UPPER_BOUND(3.0),
    WIND_SPEED_DECENT_UPPER_BOUND(8.0),

    WAVE_PERIOD_GREAT_LOWER_BOUND(11.0),
    WAVE_PERIOD_DECENT_LOWER_BOUND(8.5)
}