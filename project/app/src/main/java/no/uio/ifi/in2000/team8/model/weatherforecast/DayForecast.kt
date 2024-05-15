package no.uio.ifi.in2000.team8.model.weatherforecast

import java.time.LocalDateTime

data class DayForecast (
    val data: Map<LocalDateTime, DataAtTime> = mapOf()
)