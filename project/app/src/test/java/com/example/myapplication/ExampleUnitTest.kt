package com.example.myapplication

import com.example.myapplication.data.locationForecast.LocationForecastDataSource
import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.model.locationforecast.DataLF
import kotlinx.coroutines.runBlocking
import org.junit.Test

//import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val locationForecastDataSource = LocationForecastDataSource()
    private val locationForecastRepository = LocationForecastRepository(locationForecastDataSource)
    @Test
    fun locationForecastTimeSeriesExists() = runBlocking {
        val timeSeries: List<Pair<String, DataLF>> = locationForecastRepository.getTimeSeries()
        if (timeSeries.isNotEmpty()){
            println("----------Testen fungerer!----------")
        }else{
            println("Testen fungerer ikke")
        }

    }
    @Test
    fun testGetWindDirection() = runBlocking {
        val windDirectionList: List<Pair<String, Double>> = locationForecastRepository.getWindDirection()
        println("Test for getWindDirection kjører:")
        println("Resultat av getWindDirection: $windDirectionList")
        println("Testen kjører!")

    }
    @Test
    fun testGetWindSpeed() = runBlocking {
        val windSpeedList: List<Pair<String, Double>> = locationForecastRepository.getWindSpeed()
        println("Test for getWindSpeed kjører:")
        println("Resultat av getWindSpeed: $windSpeedList")
        println("Testen kjører!")

    }
    @Test
    fun testGetWindSpeedOfGust() = runBlocking {
        val windSpeedOfGust: List<Pair<String, Double>> = locationForecastRepository.getWindSpeedOfGust()
        println("Test for getWindSpeedOfGust kjører:")
        println("Resultat av getWindSpeedOfGust: $windSpeedOfGust")
        print("Testen kjører!")

    }

}