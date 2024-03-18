package com.example.myapplication

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplication.data.locationForecast.LocationForecastDataSource
import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import kotlinx.coroutines.runBlocking
import com.example.myapplication.model.metalerts.MetAlerts
import com.google.gson.Gson
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl


import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastDataSource
import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.SurfArea

import com.example.myapplication.model.locationforecast.LocationForecast
import com.example.myapplication.model.locationforecast.TimeserieLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF

import com.example.myapplication.model.oceanforecast.OceanForecast
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.ui.home.HomeScreenUiState
import com.example.myapplication.ui.home.HomeScreenViewModel
import kotlinx.coroutines.async
import java.io.File
import org.junit.Test
//import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    //global
    private val gson = Gson()

    //MetAlerts
    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl()
    private val metAlertJson = File("src/test/java/com/example/myapplication/metAlerts.json").readText()
    @Test
    fun testMetAlertsAreaNameWithProxy() = runBlocking {
        val feature = metAlertsRepository.getFeatures()[0]
        println(feature.properties?.area)
    }

    @Test
    fun getRelevantAlertsForFedjeByApiCall() = runBlocking {
        val relevantAlerts = metAlertsRepository.getRelevantAlertsFor(SurfArea.FEDJE)
        println(relevantAlerts)
        relevantAlerts.forEach { println("Alert: $it") }
    }

    @Test
    fun getRelevantAlertsForNordkappOnlyGetsAlertsFromAreaNordkapp() = runBlocking {
        println("Script is ran from: ${System.getProperty("user.dir")}")
        val metAlerts: MetAlerts = gson.fromJson(metAlertJson, MetAlerts::class.java)
        val features = metAlerts.features
        val relevantAlerts = metAlertsRepository.getRelevantAlertsFor(SurfArea.NORDKAPP)
        relevantAlerts.forEach { assert(it.properties?.area?.lowercase()?.contains("nordkapp") == true) }
    }

    //Ocean forecast
    private val oceanforecastRepository = OceanforecastRepositoryImpl()
    private val oceanforecastJson = File("src/test/java/com/example/myapplication/OceanforecastHoddevik.json").readText()

    //TODO: usikker på hvordan teste getTimeSeries() på en statisk måte
    @Test
    fun testGetWaveHeight() = runBlocking{
        val oceanForecast: OceanForecast = gson.fromJson(oceanforecastJson, OceanForecast::class.java)
        val timeSeriesList: List<TimeserieOF> = oceanForecast.properties.timeseries
        val timeSeries = timeSeriesList.map { it.time to it.data }

        val time0 = timeSeries[0].first
        assert(time0 == "2024-03-13T17:00:00Z")

        //sjekker om bølgehøyden er lik ved å direkte aksessere den i filen og ved å bruke repositoryet sin get-metode for bølgehøyde
        //assert(timeSeries[0].second.instant.details.sea_surface_wave_height == oceanforecastRepository.getWaveHeights()[0].second)
        //assert(timeSeries[10].second.instant.details.sea_surface_wave_height == oceanforecastRepository.getWaveHeights()[10].second)

    }
    
    //Location Forecast
    private val locationForecastRepository = LocationForecastRepositoryImpl()

    fun testWindDirection() = runBlocking {
        val locationJson = File("src/test/java/com/example/myapplication/locationForecast.json").readText()
        val locationForecast: LocationForecast = gson.fromJson(locationJson, LocationForecast::class.java)
        val timeseriesListLF: List<TimeserieLF> = locationForecast.properties.timeseries
        val timeSeriesLF = timeseriesListLF.map { it.time to it.data }
        val windDirectionForecast = locationForecastRepository.getWindDirection()
        assert(timeSeriesLF[0].second.instant.details.wind_from_direction == windDirectionForecast[0].second)
        assert(timeSeriesLF[10].second.instant.details.wind_from_direction == windDirectionForecast[10].second)
    }

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

    //SmackLipRepository
    private val smackLipRepository : SmackLipRepository = SmackLipRepositoryImpl()
    @Test
    fun testGetDateFromTimeString() {
        val timeList = smackLipRepository.getTimeListFromTimeString("2024-03-13T19:00:00Z")
        assert(timeList[0] == 2024)
        assert(timeList[1] == 3)
        assert(timeList[2] == 13)
        assert(timeList[3] == 19)

    }


    @Test
    fun testGetWaveHeightsSmackLip() = runBlocking {
        //henter data fra API, må sjekke i API om det stemmer
        println(smackLipRepository.getWaveHeights()[0].first.toString())
        println(smackLipRepository.getWaveHeights()[0].second)
    }

    @Test
    fun testGetWindDirectionSmackLip() = runBlocking {
        println(smackLipRepository.getWindDirection()[0].first.toString())
        println(smackLipRepository.getWindDirection()[0].second)
    }

    @Test
    fun testGetWindSpeedSmackLip() = runBlocking {
        println(smackLipRepository.getWindSpeed()[0].first.toString())
        println(smackLipRepository.getWindSpeed()[0].second)
    }



    @Test
    fun testGetWindSpeedOfGustSmackLip() = runBlocking {
        println(smackLipRepository.getWindSpeedOfGust()[0].first.toString())
        println(smackLipRepository.getWindSpeedOfGust()[0].second)
    }

    @Test
    fun testGetForecastNext24Hours() = runBlocking {
        val tmp : MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>> = smackLipRepository.getForecastNext24Hours()

        println(smackLipRepository.getForecastNext24Hours().toString())
    }

}