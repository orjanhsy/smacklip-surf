package com.example.myapplication

import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import kotlinx.coroutines.runBlocking
import com.example.myapplication.model.metalerts.MetAlerts
import com.google.gson.Gson
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl


import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl

import com.example.myapplication.data.waveforecast.WaveForecastDataSource
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea


import com.example.myapplication.model.locationforecast.LocationForecast
import com.example.myapplication.model.locationforecast.TimeserieLF

import com.example.myapplication.model.oceanforecast.OceanForecast
import com.example.myapplication.model.oceanforecast.TimeserieOF
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.VersionCheckResult
import junit.framework.TestCase.assertEquals
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

    //WaveForecast
    private val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl()
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()

    @Test
    fun fetchAvaliableTimestampsReturns20ForecastTimes() = runBlocking {
        val response = waveForecastDataSource.fetchAvaliableTimestamps()
        assert(response.availableForecastTimes.size == 20) // may vary
    }

    @Test
    fun pointForecastNext3DaysHasForecastsOfLength20() = runBlocking {
        val allPointForecastsNext3Days = waveForecastRepository.pointForecastNext3Days()
        allPointForecastsNext3Days.forEach {
            println("${it.key} -> ${it.value}")
            assert(it.value.size == 20) {"Length of forecast was ${it.value.size}, should be 20"}
        }


    }

    @Test
    fun accessTokenIsAcquired() = runBlocking{
        val (accessToken, refreshToken) = waveForecastDataSource.getAccessToken()
        assert(accessToken.isNotBlank())
    }

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
        val windDirectionForecast = locationForecastRepository.getWindDirection(SurfArea.FEDJE)
        assert(timeSeriesLF[0].second.instant.details.wind_from_direction == windDirectionForecast[0].second)
        assert(timeSeriesLF[10].second.instant.details.wind_from_direction == windDirectionForecast[10].second)
    }

    @Test
    fun locationForecastTimeSeriesExists() = runBlocking {
        val timeSeries: List<Pair<String, DataLF>> = locationForecastRepository.getTimeSeries(SurfArea.FEDJE)
        if (timeSeries.isNotEmpty()){
            println("----------Testen fungerer!----------")
        }else{
            println("Testen fungerer ikke")
        }

    }
    @Test
    fun testGetWindDirection() = runBlocking {
        val windDirectionList: List<Pair<String, Double>> = locationForecastRepository.getWindDirection(SurfArea.FEDJE)
        println("Test for getWindDirection kjører:")
        println("Resultat av getWindDirection: $windDirectionList")
        println("Testen kjører!")
    }

    @Test
    fun testGetWindSpeed() = runBlocking {
        val windSpeedList: List<Pair<String, Double>> = locationForecastRepository.getWindSpeed(SurfArea.FEDJE)
        println("Test for getWindSpeed kjører:")
        println("Resultat av getWindSpeed: $windSpeedList")
        println("Testen kjører!")

    }
    @Test
    fun testGetWindSpeedOfGust() = runBlocking {
        val windSpeedOfGust: List<Pair<String, Double>> = locationForecastRepository.getWindSpeedOfGust(SurfArea.FEDJE)
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
    fun testGetWaveHeightsSmackLipNordkapp() = runBlocking {
        //henter data fra API, må sjekke i API om det stemmer
        println(smackLipRepository.getWaveHeights(SurfArea.NORDKAPP)[0].first.toString())
        println(smackLipRepository.getWaveHeights(SurfArea.NORDKAPP)[0].second)

    }
    @Test
    fun testGetWaveHeightsSmackLipErvika() = runBlocking {
        println("Ervika:")
        println(smackLipRepository.getWaveHeights(SurfArea.ERVIKA)[0].first)
        println(smackLipRepository.getWaveHeights(SurfArea.ERVIKA)[0].second)
        println("Hoddevik:")
        println(smackLipRepository.getWaveHeights(SurfArea.HODDEVIK)[0].first)
        println(smackLipRepository.getWaveHeights(SurfArea.HODDEVIK)[0].second)

    }


    @Test
    fun testGetWaveHeightsOF() = runBlocking {
        println("Test Ervika:")
        println(oceanforecastRepository.getWaveHeights(SurfArea.ERVIKA)[0].first.toString())
        println("Bølgehøyde Ervika test:" + oceanforecastRepository.getWaveHeights(SurfArea.ERVIKA)[0].second)
        println("Test Hoddevik:")
        println(oceanforecastRepository.getWaveHeights(SurfArea.HODDEVIK)[0].first.toString())
        println("Bølgehøyde Hoddevik test:" + oceanforecastRepository.getWaveHeights(SurfArea.HODDEVIK)[0].second)

    }

    @Test
    fun testGetWindSpeedLF() = runBlocking {
        println("test Ervika:")
        println(locationForecastRepository.getWindSpeed(SurfArea.ERVIKA)[0].first.toString())
        println(locationForecastRepository.getWindSpeed(SurfArea.ERVIKA)[0].second)
        println("test Hoddevik:")
        println(locationForecastRepository.getWindSpeed(SurfArea.HODDEVIK)[0].first.toString())
        println(locationForecastRepository.getWindSpeed(SurfArea.HODDEVIK)[0].second)
    }


    @Test
    fun testGetWindDirectionSmackLip() = runBlocking {
        println(smackLipRepository.getWindDirection(SurfArea.FEDJE)[0].first.toString())
        println(smackLipRepository.getWindDirection(SurfArea.FEDJE)[0].second)
    }

    @Test
    fun testGetWindSpeedSmackLip() = runBlocking {
        println(smackLipRepository.getWindSpeed(SurfArea.FEDJE)[0].first.toString())
        println(smackLipRepository.getWindSpeed(SurfArea.FEDJE)[0].second)
    }



    @Test
    fun testGetWindSpeedOfGustSmackLip() = runBlocking {
        println(smackLipRepository.getWindSpeedOfGust(SurfArea.FEDJE)[0].first.toString())
        println(smackLipRepository.getWindSpeedOfGust(SurfArea.FEDJE)[0].second)
    }

    /*
    @Test
    fun testGetForecastNext24Hours() = runBlocking {
        val tmp : MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>> = smackLipRepository.getForecastNext24Hours()

        println(smackLipRepository.getForecastNext24Hours().toString())
    }

     */

    /*
    @Test
    fun testGetDataForOneDay() = runBlocking {
        println(smackLipRepository.getDataForOneDay(19))
        println(smackLipRepository.getDataForOneDay(20))
        println(smackLipRepository.getDataForOneDay(21))
        println(smackLipRepository.getDataForOneDay(22))
        println(smackLipRepository.getDataForOneDay(23))
    }

    @Test
    fun testGetDataFor7Days() = runBlocking {
        println(smackLipRepository.getDataForTheNext7Days())
    }

     */
}