package com.example.myapplication

import com.example.myapplication.data.locationForecast.LocationForecastDataSource
import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.model.locationforecast.Data
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val locationForecastDataSource = LocationForecastDataSource()
    private val locationForecastRepository = LocationForecastRepository(locationForecastDataSource)
    @Test
    fun addition_isCorrect() = runBlocking {
        val timeSeries: List<Pair<String, Data>> = locationForecastRepository.getTimeSeries()
        val time1 = timeSeries.get(0).first

        //assertEquals(4, 2 + 2)
        print("$time1 ----------Testen fungerer!----------")
    }
}