package com.example.myapplication.utils


import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.conditions.Conditions
import com.example.myapplication.model.surfareas.SurfArea
import kotlin.math.abs

class ConditionUtils {

    // measures conditionStatus as the mean "value" of conditions
     fun getConditionStatus(
        location: SurfArea?,
        wavePeriod: Double?,
        windSpeed: Double,
        windDir: Double,
        waveHeight: Double,
        waveDir: Double,
    ): ConditionStatus {

         // does not report conditionStatus when wavePeriods are not forecast
        if (wavePeriod == null || location == null) {
            return ConditionStatus.BLANK
        }

        // always poor if:
        if (wavePeriod <= Conditions.WAVE_PERIOD_LOWER_BOUND.value || windSpeed >= Conditions.WIND_SPEED_UPPER_BOUND.value ||
            waveHeight <= Conditions.WAVE_HEIGHT_LOWER_BOUND.value || waveHeight >= Conditions.WAVE_HEIGHT_UPPER_BOUND.value
        ) {
            return ConditionStatus.POOR
        }

        val status = mutableMapOf<String, Double>()

        // Evaluate wind speed condition
        status["windSpeed"] = when {
            windSpeed < Conditions.WIND_SPEED_GREAT_UPPER_BOUND.value -> 1.0
            windSpeed < Conditions.WIND_SPEED_DECENT_UPPER_BOUND.value -> 2.0
            else -> 3.0
        }

        // Apply wind direction factor to wind speed condition
        status["windSpeed"] = status["windSpeed"]!! * evaluateWindDir(location.optimalWindDir, windDir)

        // Evaluate wave direction condition
        status["waveDir"] = when {
            withinDir(location.optimalWaveDir, waveDir, Conditions.WAVE_DIR_GREAT_DEVIATION.value) -> 1.0
            withinDir(location.optimalWaveDir, waveDir, Conditions.WAVE_DIR_DECENT_DEVIATION.value) -> 2.0
            else -> 3.0
        }

        // Evaluate wave period condition
        status["wavePeriod"] = when {
            wavePeriod > Conditions.WAVE_PERIOD_GREAT_LOWER_BOUND.value -> 1.0
            wavePeriod >= Conditions.WAVE_PERIOD_DECENT_LOWER_BOUND.value -> 2.0
            else -> 3.0
        }

        // Calculate average status
        val averageStatus = status.values.sum() / status.size

        // Determine overall condition status
        return when {
            averageStatus < 1.3 -> ConditionStatus.GREAT
            averageStatus in 1.3 .. 2.3 -> ConditionStatus.DECENT
            else -> ConditionStatus.POOR
        }
    }
    
    private fun withinDir(optimalDir: Double, actualDir: Double, acceptedOffset: Double): Boolean {

        return abs(optimalDir - actualDir) !in acceptedOffset .. 360 - acceptedOffset
    }

    private fun evaluateWindDir(optimalDir: Double, actualDir: Double): Double {
        val normalizedDiff = Math.min(abs(optimalDir - actualDir) % 360, abs(actualDir - optimalDir) % 360)
        return when {
            normalizedDiff <= Conditions.WIND_DIR_GREAT_DEVIATION.value -> 1.0
            normalizedDiff >= 180 - Conditions.WIND_DIR_GREAT_DEVIATION.value && normalizedDiff <= 180 + Conditions.WIND_DIR_GREAT_DEVIATION.value -> 1.1
            else -> 1.5
        }
    }

}


