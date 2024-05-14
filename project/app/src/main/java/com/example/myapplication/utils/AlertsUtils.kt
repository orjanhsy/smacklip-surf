package com.example.myapplication.utils

import com.example.myapplication.R

class AlertsUtils {
    
    fun getIconBasedOnAwarenessLevel(awarenessLevel: String): Int {
        return try {
            if (awarenessLevel.isNotEmpty()) {
                val firstChar = awarenessLevel.firstOrNull()?.toString()

                when (firstChar) {
                    "2" -> R.drawable.icon_warning_yellow
                    "3" -> R.drawable.icon_warning_orange
                    "4" -> R.drawable.icon_warning_red
                    else -> R.drawable.icon_awareness_default // If awarenessLevel is not 2, 3, or 4
                }
            } else {
                R.drawable.icon_awareness_default
            }
        } catch (e: Exception) {
            R.drawable.icon_awareness_default
        }
    }
}