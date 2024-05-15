package com.example.myapplication

import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.weatherforecast.WeatherForecastRepositoryImpl
import com.example.myapplication.utils.DateUtils
import com.example.myapplication.utils.RecourseUtils
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.format.DateTimeFormatter

class UtilTests {

    @Test
    fun symbolCodeIsInResourceUtils(): Unit = runBlocking{
        val repo = WeatherForecastRepositoryImpl()
        repo.loadOFlF()
        val resourceUtils = RecourseUtils()
        val ofLfForecast = repo.ofLfForecast.value
        ofLfForecast.next7Days.values.map {week ->
            week.forecast.map {day ->
                day.data.values.map {
                    assert(resourceUtils.findWeatherSymbol(it.symbolCode) != R.drawable.spm)
                }
            }
        }
    }

    @Test
    fun dateUtilFormatsAsExpected()= runBlocking{
        val dateUtils = DateUtils()
        val timeInterval = listOf("2024-05-12T06:00:00+00:00", "2024-05-19T22:00:00+00:00")
        assert(dateUtils.formatTimeInterval(timeInterval) == "12.Mai - 19.Mai") {"Was ${dateUtils.formatTimeInterval(timeInterval)}, should be '12.Mai - 19.Mai'"}
    }
}