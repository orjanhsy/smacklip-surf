package com.example.myapplication.model.conditions

enum class Conditions(val value: Double) {
    // always poor if:
    WAVE_HEIGHT_LOWER_BOUND(0.3),
    WAVE_HEIGHT_UPPER_BOUND(6.0),
    WIND_SPEED_UPPER_BOUND(17.0),
    WAVE_PERIOD_LOWER_BOUND(6.0),


    WIND_SPEED_GREAT_UPPER_BOUND(4.0),
    WIND_SPEED_DECENT_UPPER_BOUND(8.0),
    WIND_DIR_GREAT_DEVIATION(30.0),

    WAVE_PERIOD_GREAT_LOWER_BOUND(9.0),
    WAVE_PERIOD_DECENT_LOWER_BOUND(7.0),
    WAVE_DIR_GREAT_DEVIATION(25.0),
    WAVE_DIR_DECENT_DEVIATION(45.0),

}