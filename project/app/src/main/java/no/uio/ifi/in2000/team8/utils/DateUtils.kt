package com.example.myapplication.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateUtils {
    fun formatTimeInterval(interval:List<String>?): String {
        if (interval == null || interval.isEmpty()) return "Time not available"

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val firstDate = LocalDateTime.parse(interval.firstOrNull(), formatter)
        val lastDate = LocalDateTime.parse(interval.lastOrNull(), formatter)

        val firstDay = firstDate.dayOfMonth
        val firstMonth = getMonthAbbreviation(firstDate.monthValue)

        val lastDay = lastDate.dayOfMonth
        val lastMonth = getMonthAbbreviation(lastDate.monthValue)

        return "$firstDay.$firstMonth - $lastDay.$lastMonth"
    }

    private fun getMonthAbbreviation(monthValue: Int): String {
        val monthsAbbreviations = mapOf(
            1 to "Jan", 2 to "Feb", 3 to "Mar", 4 to "Apr",
            5 to "Mai", 6 to "Jun", 7 to "Jul", 8 to "Aug",
            9 to "Sep", 10 to "Okt", 11 to "Nov", 12 to "Des"
        )
        return monthsAbbreviations[monthValue] ?: ""
    }


}