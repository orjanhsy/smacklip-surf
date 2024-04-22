package com.example.myapplication


import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsDataSource
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastDataSource
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.LocationForecast
import com.example.myapplication.model.locationforecast.TimeserieLF
import com.example.myapplication.model.oceanforecast.OceanForecast
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.model.surfareas.SurfArea
import com.google.gson.Gson
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File

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
        println("her:")
        println(response)
        assert(response.availableForecastTimes.size > 0) {
            "fetchAvailableTimestamps() returns no timestamps"
        }
    }

    @Test
    fun accessTokenIsAcquired() = runBlocking {
        val (accessToken, refreshToken) = waveForecastDataSource.getAccessToken()
        assert(accessToken.isNotBlank()) {"No token was retrieved"}
        println("her:")
        println(accessToken)
    }

    //fast ver.
    @Test
    fun allRelevantWavePeriodAndDirsGet60HrsOfData() = runBlocking {
        val relevantForecasts = waveForecastRepository.allRelevantWavePeriodAndDirNext3Days()
        relevantForecasts.forEach {
            println(it)
        }
        assert(relevantForecasts.size == SurfArea.entries.size) {"Missing forecast(s) for certain surfarea(s)"}
        assert(relevantForecasts.all { (_, forecast) -> forecast.size in 19 .. 21 }) {"Some forecast is not of length 21 (ie. 60hrs long)"}
    }

    @Test
    fun waveDirAndPeriodNext3DaysForHoddevikIs3DaysLong() = runBlocking{
        val result = waveForecastRepository.waveDirAndPeriodNext3DaysForArea(SurfArea.HODDEVIK.modelName, SurfArea.HODDEVIK.pointId)
        assert(result.size in 19 .. 21) {"Forecast for hoddevik should be of size 21, was ${result.size}"}
    }

    @Test
    fun hardcodedWaveForecastIsSameAsNonHardcoded() = runBlocking{
        val hardcoded = waveForecastRepository.allRelevantWavePeriodAndDirNext3DaysHardCoded()
        val nonHardcoded = waveForecastRepository.allRelevantWavePeriodAndDirNext3Days()
        assert(hardcoded==nonHardcoded)
    }

    @Test
    fun smackLipWavePeriodsForAreaAreSize60()= runBlocking{
        val wavePeriods = smackLipRepository.getWavePeriodsNext3DaysForArea(SurfArea.HODDEVIK)
        assert(wavePeriods.size in 57 .. 63) {"was size ${wavePeriods.size}"}
        println(wavePeriods)
        assert(wavePeriods[0] == wavePeriods[1] && wavePeriods[1] == wavePeriods[2]) {"${wavePeriods[0]}, ${wavePeriods[1]}, ${wavePeriods[2]} differ"}
    }

    @Test
    fun smackLipAllWavePeriodsAreSize60() = runBlocking{
        val wavePeriods = smackLipRepository.getAllWavePeriodsNext3Days()
        wavePeriods.forEach{(sa, tps) ->
            assert(tps.size in 57..63) {"Size of $sa was ${tps.size}"}
            assert(tps[0] == tps[1] && tps[1] == tps[2]) {"${tps[0]}, ${tps[1]}, ${tps[2]} differ"}
        }
    }

    @Test
    fun retrievesRelevantModelNamesAndPointIdsWorks() = runBlocking{
        val data = waveForecastRepository.retrieveRelevantModelNamesAndPointIds()
        println(data)
    }

    @Test
    fun distanceToIsCorrectForAreas() = runBlocking{
        val allPointForecast = SurfArea.entries.map { it to waveForecastRepository.pointForecast(it.modelName, it.pointId, time="2024-04-14T12:00:00Z")}
        allPointForecast.forEach { (area, pointForecast) ->
            val result = waveForecastRepository.distanceTo(
                pointForecast.lat,
                pointForecast.lon,
                area
            )
            println(area.locationName)
            println("Modelname: ${pointForecast.modelName} ID: ${pointForecast.idNumber}")
            println("Distance from point (${pointForecast.lat}, ${pointForecast.lon}) to (${area.lat}, ${area.lon}) was $${result}km\n")
        }
    }


    //MetAlerts
    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl()
    private val metAlertsDataSource: MetAlertsDataSource = MetAlertsDataSource()

    @Test
    fun metAlertsContainGeoDataIfNotEmpty() = runBlocking {
        SurfArea.entries.forEach {
            val alerts = metAlertsRepository.getRelevantAlertsFor(it)
            assert(if (alerts.isNotEmpty()) alerts.all{alert ->
                alert.geometry?.type == "Polygon" || alert.geometry?.type == "MultiPolygon"
            } else true
            ) {
                "Metalerts provided data, however the geodata type was incorrect"
            }
        }
    }

    @Test
    fun relevantAlertsDoesNotLeaveOutAnyRelevantAlerts(): Unit = runBlocking {
        val relevantAlerts = SurfArea.entries.map { it to metAlertsRepository.getRelevantAlertsFor(it) }
        val allAlerts = metAlertsDataSource.fetchMetAlertsData().features
        relevantAlerts.map {(area, alerts) ->
            val sizeAll = allAlerts.filter {alert ->
                alert.properties?.area?.contains(area.locationName) ?: false
            }.size
            assert(sizeAll <= alerts.size) {
                "All alerts contains $sizeAll alerts for ${area.locationName}, relevantAlerts() recovered ${alerts.size} of them"
            }
        }
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

    @Test
    fun getWaveHeightIsNotEmptyForHoddevik()= runBlocking{
        val data = oceanforecastRepository.getWaveDirections(SurfArea.HODDEVIK)
        assert(data.isNotEmpty())
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
        assertTrue("Time series should not be empty", timeSeries.isNotEmpty())
    }

    @Test
    fun testGetWindDirection() = runBlocking {
        val windDirectionList: List<Pair<String, Double>> = locationForecastRepository.getWindDirection(SurfArea.FEDJE)
        assertNotNull("Wind direction list should not be null", windDirectionList)
        assertFalse("Wind direction list should not be empty", windDirectionList.isEmpty())
    }

    @Test
    fun testGetWindSpeed() = runBlocking {
        val windSpeedList: List<Pair<String, Double>> = locationForecastRepository.getWindSpeed(SurfArea.FEDJE)
        assertNotNull("Wind speed should not be null", windSpeedList)
        assertFalse("Wind speed should not be empty", windSpeedList.isEmpty())
    }

    @Test
    fun testGetWindSpeedOfGust() = runBlocking {
        val windSpeedOfGust: List<Pair<String, Double>> = locationForecastRepository.getWindSpeedOfGust(SurfArea.FEDJE)
        assertNotNull("Wind speed of gust should not be null", windSpeedOfGust)
        assertFalse("Wind speed of gust should not be empty", windSpeedOfGust.isEmpty())
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
    fun waveHeightsAreParsedCorrectly() = runBlocking {
        val dataNext7 = smackLipRepository.getDataForTheNext7Days(SurfArea.HODDEVIK)
        val waveHeights = dataNext7.map{ dayForecast -> dayForecast.map { dayData -> dayData.first to dayData.second[0]}}
        println("Size of all wave heights ${waveHeights.size}")
        println("Size of waveheighs one step in ${waveHeights[1]}")

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

    @Test
    fun testGetSymbolCode() = runBlocking{
        println(smackLipRepository.getSymbolCode(surfArea = SurfArea.HODDEVIK))
    }

    @Test
    fun testGetDataForOneDay() = runBlocking {

        println(smackLipRepository.getDataForOneDay(20, SurfArea.HODDEVIK))
        println(smackLipRepository.getDataForOneDay(21, SurfArea.HODDEVIK))
        println(smackLipRepository.getDataForOneDay(22, SurfArea.HODDEVIK))
        println(smackLipRepository.getDataForOneDay(23, SurfArea.HODDEVIK))
    }


    @Test
    fun testGetDataFor7Days() = runBlocking {
        println(smackLipRepository.getDataForTheNext7Days(SurfArea.HODDEVIK))
    }



}
