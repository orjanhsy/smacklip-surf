//package no.uio.ifi.in2000.team8
//
//
//import androidx.datastore.core.DataStore
//import no.uio.ifi.in2000.team8.data.locationForecast.LocationForecastRepositoryImpl
//import no.uio.ifi.in2000.team8.data.metalerts.MetAlertsDataSource
//import no.uio.ifi.in2000.team8.data.metalerts.MetAlertsRepositoryImpl
//import no.uio.ifi.in2000.team8.data.oceanforecast.OceanforecastRepositoryImpl
//import no.uio.ifi.in2000.team8.data.settings.SettingsRepository
//import no.uio.ifi.in2000.team8.data.settings.SettingsRepositoryImpl
//import no.uio.ifi.in2000.team8.data.settings.SettingsSerializer
//import no.uio.ifi.in2000.team8.data.smackLip.SmackLipRepository
//import no.uio.ifi.in2000.team8.data.smackLip.SmackLipRepositoryImpl
//import no.uio.ifi.in2000.team8.data.waveforecast.WaveForecastDataSource
//import no.uio.ifi.in2000.team8.data.waveforecast.WaveForecastRepository
//import no.uio.ifi.in2000.team8.data.waveforecast.WaveForecastRepositoryImpl
//import no.uio.ifi.in2000.team8.model.locationforecast.DataLF
//import no.uio.ifi.in2000.team8.model.locationforecast.LocationForecast
//import no.uio.ifi.in2000.team8.model.locationforecast.TimeserieLF
//import no.uio.ifi.in2000.team8.model.oceanforecast.OceanForecast
//import no.uio.ifi.in2000.team8.model.oceanforecast.TimeserieOF
//import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
//import com.google.gson.Gson
//import junit.framework.TestCase.assertEquals
//import junit.framework.TestCase.assertFalse
//import junit.framework.TestCase.assertNotNull
//import junit.framework.TestCase.assertTrue
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.runBlocking
//import org.junit.Before
//import org.junit.Test
//import java.io.ByteArrayInputStream
//import java.io.ByteArrayOutputStream
//import java.io.File
//import java.time.LocalDate
//import kotlin.system.measureTimeMillis
//
////import org.junit.Assert.*
//
///**
// * Example local unit test, which will execute on the development machine (host).
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
//class ExampleUnitTest {
//
//    //global
//    private val gson = Gson()
//
//    //WaveForecast
//    private val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl()
//    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()
//
//    @Test
//    fun fetchAvaliableTimestampsReturns20ForecastTimes() = runBlocking {
//        val response = waveForecastDataSource.fetchAvaliableTimestamps()
//        println("her:")
//        println(response)
//        assert(response.availableForecastTimes.size > 0) {
//            "fetchAvailableTimestamps() returns no timestamps"
//        }
//    }
//
//    @Test
//    fun accessTokenIsAcquired() = runBlocking {
//        val (accessToken, refreshToken) = waveForecastDataSource.getAccessToken()
//        assert(accessToken.isNotBlank()) {"No token was retrieved"}
//        println("her:")
//        println(accessToken)
//    }
//
//    //fast ver.
//    @Test
//    fun allRelevantWavePeriodAndDirsGet60HrsOfData() = runBlocking {
//        val relevantForecasts = waveForecastRepository.allRelevantWavePeriodsNext3DaysHardCoded()
//        relevantForecasts.forEach {
//            println(it)
//        }
//        assert(relevantForecasts.size == SurfArea.entries.size) {"Missing forecast(s) for certain surfarea(s)"}
//        assert(relevantForecasts.all { (_, forecast) -> forecast.size in 19 .. 21 }) {"Some forecast is not of length 21 (ie. 60hrs long)"}
//    }
//
//    @Test
//    fun waveDirAndPeriodNext3DaysForHoddevikIs3DaysLong() = runBlocking{
//        val result = waveForecastRepository.wavePeriodsNext3DaysForArea(SurfArea.HODDEVIK.modelName, SurfArea.HODDEVIK.pointId)
//        assert(result.size in 19 .. 21) {"Forecast for hoddevik should be of size 21, was ${result.size}"}
//    }
//
//    @Test
//    fun smackLipWavePeriodsForAreaAreSize60()= runBlocking{
//        val wavePeriods = smackLipRepository.getWavePeriodsNext3DaysForArea(SurfArea.HODDEVIK)
//        assert(wavePeriods.size in 57 .. 63) {"was size ${wavePeriods.size}"}
//        println(wavePeriods)
//        assert(wavePeriods[0] == wavePeriods[1] && wavePeriods[1] == wavePeriods[2]) {"${wavePeriods[0]}, ${wavePeriods[1]}, ${wavePeriods[2]} differ"}
//    }
//
//    @Test
//    fun smackLipAllWavePeriodsAreSize60() = runBlocking{
//        val wavePeriods = smackLipRepository.getAllWavePeriodsNext3Days()
//        assert(wavePeriods.isNotEmpty())
//        wavePeriods.forEach{(sa, tps) ->
//            assert(tps.size in 57..63) {"Size of $sa was ${tps.size}"}
//            assert(tps[0] == tps[1] && tps[1] == tps[2]) {"${tps[0]}, ${tps[1]}, ${tps[2]} differ"}
//        }
//    }
//
//    @Test
//    fun retrievesRelevantModelNamesAndPointIdsWorks() = runBlocking{
//        val data = waveForecastRepository.retrieveRelevantModelNamesAndPointIds()
//        println(data)
//    }
//
//    @Test
//    fun distanceToIsCorrectForAreas() = runBlocking{
//        val allPointForecast = SurfArea.entries.map { it to waveForecastRepository.pointForecast(it.modelName, it.pointId, time="2024-04-14T12:00:00Z")}
//        allPointForecast.forEach { (area, pointForecast) ->
//            val result = waveForecastRepository.distanceTo(
//                pointForecast.lat,
//                pointForecast.lon,
//                area
//            )
//            println(area.locationName)
//            println("Modelname: ${pointForecast.modelName} ID: ${pointForecast.idNumber}")
//            println("Distance from point (${pointForecast.lat}, ${pointForecast.lon}) to (${area.lat}, ${area.lon}) was $${result}km\n")
//        }
//    }
//
//
//    //MetAlerts
//    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl()
//    private val metAlertsDataSource: MetAlertsDataSource = MetAlertsDataSource()
//
//    @Test
//    fun metAlertsContainGeoDataIfNotEmpty() = runBlocking {
//        SurfArea.entries.forEach {
//            val alerts = metAlertsRepository.getRelevantAlertsFor(it)
//            assert(if (alerts.isNotEmpty()) alerts.all{alert ->
//                alert.geometry?.type == "Polygon" || alert.geometry?.type == "MultiPolygon"
//            } else true
//            ) {
//                "Metalerts provided data, however the geodata type was incorrect"
//            }
//        }
//    }
//
//    @Test
//    fun relevantAlertsDoesNotLeaveOutAnyRelevantAlerts(): Unit = runBlocking {
//        val relevantAlerts = SurfArea.entries.map { it to metAlertsRepository.getRelevantAlertsFor(it) }
//        val allAlerts = metAlertsDataSource.fetchMetAlertsData().features
//        relevantAlerts.map {(area, alerts) ->
//            val sizeAll = allAlerts.filter {alert ->
//                alert.properties?.area?.contains(area.locationName) ?: false
//            }.size
//            assert(sizeAll <= alerts.size) {
//                "All alerts contains $sizeAll alerts for ${area.locationName}, relevantAlerts() recovered ${alerts.size} of them"
//            }
//        }
//    }
//
//
//    //Ocean forecast
//    private val oceanforecastRepository = OceanforecastRepositoryImpl()
//    private val oceanforecastJson = File("src/test/java/com/example/myapplication/OceanforecastHoddevik.json").readText()
//
//    //TODO: usikker på hvordan teste getTimeSeries() på en statisk måte
//    @Test
//    fun testGetWaveHeight() = runBlocking{
//        val oceanForecast: OceanForecast = gson.fromJson(oceanforecastJson, OceanForecast::class.java)
//        val timeSeriesList: List<TimeserieOF> = oceanForecast.properties.timeseries
//        val timeSeries = timeSeriesList.map { it.time to it.data }
//
//        val time0 = timeSeries[0].first
//        assert(time0 == "2024-03-13T17:00:00Z")
//
//        //sjekker om bølgehøyden er lik ved å direkte aksessere den i filen og ved å bruke repositoryet sin get-metode for bølgehøyde
//        //assert(timeSeries[0].second.instant.details.sea_surface_wave_height == oceanforecastRepository.getWaveHeights()[0].second)
//        //assert(timeSeries[10].second.instant.details.sea_surface_wave_height == oceanforecastRepository.getWaveHeights()[10].second)
//    }
//
//    @Test
//    fun getWaveHeightIsNotEmptyForHoddevik()= runBlocking{
//        val data = oceanforecastRepository.getWaveDirections(SurfArea.HODDEVIK)
//        assert(data.isNotEmpty())
//    }
//
//    //Location Forecast
//    private val locationForecastRepository = LocationForecastRepositoryImpl()
//
//    fun testWindDirection() = runBlocking {
//        val locationJson = File("src/test/java/com/example/myapplication/locationForecast.json").readText()
//        val locationForecast: LocationForecast = gson.fromJson(locationJson, LocationForecast::class.java)
//        val timeseriesListLF: List<TimeserieLF> = locationForecast.properties.timeseries
//        val timeSeriesLF = timeseriesListLF.map { it.time to it.data }
//        val windDirectionForecast = locationForecastRepository.getWindDirection(SurfArea.HODDEVIK)
//        assert(timeSeriesLF[0].second.instant.details.wind_from_direction == windDirectionForecast[0].second)
//        assert(timeSeriesLF[10].second.instant.details.wind_from_direction == windDirectionForecast[10].second)
//    }
//
//    @Test
//    fun locationForecastTimeSeriesExists() = runBlocking {
//        val timeSeries: List<Pair<String, DataLF>> = locationForecastRepository.getTimeSeries(SurfArea.HODDEVIK)
//        assertTrue("Time series should not be empty", timeSeries.isNotEmpty())
//    }
//
//    @Test
//    fun testGetWindDirection() = runBlocking {
//        val windDirectionList: List<Pair<String, Double>> = locationForecastRepository.getWindDirection(SurfArea.HODDEVIK)
//        assertNotNull("Wind direction list should not be null", windDirectionList)
//        assertFalse("Wind direction list should not be empty", windDirectionList.isEmpty())
//    }
//
//    @Test
//    fun testGetWindSpeed() = runBlocking {
//        val windSpeedList: List<Pair<String, Double>> = locationForecastRepository.getWindSpeed(SurfArea.HODDEVIK)
//        assertNotNull("Wind speed should not be null", windSpeedList)
//        assertFalse("Wind speed should not be empty", windSpeedList.isEmpty())
//    }
//
//    @Test
//    fun testGetWindSpeedOfGust() = runBlocking {
//        val windSpeedOfGust: List<Pair<String, Double>> = locationForecastRepository.getWindSpeedOfGust(SurfArea.HODDEVIK)
//        assertNotNull("Wind speed of gust should not be null", windSpeedOfGust)
//        assertFalse("Wind speed of gust should not be empty", windSpeedOfGust.isEmpty())
//    }
//
//    //SmackLipRepository
//    private val smackLipRepository : SmackLipRepository = SmackLipRepositoryImpl()
//    @Test
//    fun testGetDateFromTimeString() {
//        val timeList = smackLipRepository.getTimeListFromTimeString("2024-03-13T19:00:00Z")
//        assert(timeList[0] == 2024)
//        assert(timeList[1] == 3)
//        assert(timeList[2] == 13)
//        assert(timeList[3] == 19)
//
//    }
//
//    @Test
//    fun testGetTimeSeriesOFLF(): Unit = runBlocking {
//
//        SurfArea.entries.map {
//            println(smackLipRepository.getTimeSeriesOFLF(it))
//        }
//    }
//
//    @Test
//    fun testGetWaveHeightsSmackLipErvika() = runBlocking {
//        println("Ervika:")
//        println(smackLipRepository.getWaveHeights(SurfArea.ERVIKA)[0].first)
//        println(smackLipRepository.getWaveHeights(SurfArea.ERVIKA)[0].second)
//        println("Hoddevik:")
//        println(smackLipRepository.getWaveHeights(SurfArea.HODDEVIK)[0].first)
//        println(smackLipRepository.getWaveHeights(SurfArea.HODDEVIK)[0].second)
//
//    }
//
//
//    @Test
//    fun testGetWaveHeightsOF() = runBlocking {
//        println("Test Ervika:")
//        println(oceanforecastRepository.getWaveHeights(SurfArea.ERVIKA)[0].first.toString())
//        println("Bølgehøyde Ervika test:" + oceanforecastRepository.getWaveHeights(SurfArea.ERVIKA)[0].second)
//        println("Test Hoddevik:")
//        println(oceanforecastRepository.getWaveHeights(SurfArea.HODDEVIK)[0].first.toString())
//        println("Bølgehøyde Hoddevik test:" + oceanforecastRepository.getWaveHeights(SurfArea.HODDEVIK)[0].second)
//
//    }
//
//    @Test
//    fun testGetWindSpeedLF() = runBlocking {
//        println("test Ervika:")
//        println(locationForecastRepository.getWindSpeed(SurfArea.ERVIKA)[0].first.toString())
//        println(locationForecastRepository.getWindSpeed(SurfArea.ERVIKA)[0].second)
//        println("test Hoddevik:")
//        println(locationForecastRepository.getWindSpeed(SurfArea.HODDEVIK)[0].first.toString())
//        println(locationForecastRepository.getWindSpeed(SurfArea.HODDEVIK)[0].second)
//    }
//
//
//    @Test
//    fun testGetSymbolCode() = runBlocking{
//        println(smackLipRepository.getSymbolCode(surfArea = SurfArea.HODDEVIK))
//    }
//
////    @Test
////    fun testGetDataForOneDay() = runBlocking {
////
////        println(smackLipRepository.getDataForOneDay(20, SurfArea.HODDEVIK))
////        println(smackLipRepository.getDataForOneDay(21, SurfArea.HODDEVIK))
////        println(smackLipRepository.getDataForOneDay(22, SurfArea.HODDEVIK))
////        println(smackLipRepository.getDataForOneDay(23, SurfArea.HODDEVIK))
////    }
//
//
////    @Test
////    fun testGetDataFor7Days() = runBlocking {
////        println(smackLipRepository.getDataForTheNext7Days(SurfArea.HODDEVIK))
////    }
//
//
//    //Async calls
//
//    @Test
//    fun  testAsyncCalls(): Unit = runBlocking{
//
//       val time = measureTimeMillis { smackLipRepository.getAllOFLF7Days().forEach{
//           println(it.value.size)}}
//        println(time)
//
//    }
//
//    @Test
//    fun speedOfGetTimeseries()  = runBlocking {
//        val time2 = measureTimeMillis {
//
//            SurfArea.entries.forEach {
//                val timeseries = smackLipRepository.getTimeSeriesOFLF(it)
//                val date = LocalDate.now()
//                val xd = smackLipRepository.getOFLFOneDay(date.dayOfMonth, date.monthValue, timeseries)
//
//            }
//        }
//        val time1 = measureTimeMillis {
//            SurfArea.entries.forEach {
//                val he = smackLipRepository.getTimeSeriesOFLF(it)
//
//            }
//        }
//
//
//        println("Time1: $time1 ")
//        println("Time2: $time2 ")
//
//    }
//
//    //tester protobuf-filen
//    private lateinit var settingsStore: DataStore<Settings>
//    private lateinit var settingsRepository: SettingsRepository
//
//    @Before
//    fun setUp(){
//        settingsStore = createDataStore()
//        settingsRepository = SettingsRepositoryImpl(settingsStore)
//    }
//    private fun createDataStore(): DataStore<Settings>{
//        val settings = Settings.newBuilder().setTest(0.0).setDarkMode(false).build()
//        val settingsFlow = MutableStateFlow(settings)
//        return object : DataStore<Settings> {
//            override suspend fun updateData(transform: suspend (t: Settings) -> Settings):Settings {
//                val updatedSettings = transform(settingsFlow.value)
//                settingsFlow.value = updatedSettings
//                return updatedSettings
//            }
//
//            override val data: Flow<Settings> = settingsFlow
//        }
//
//    }
//
//
//
//    @Test
//    fun testSetTest() = runBlocking {
//        val testValue = 10.0
//        settingsRepository.setTest(testValue)
//
//        val updatedSettings = settingsStore.data.first()
//        assertEquals(testValue, updatedSettings.test, 0.0)
//    }
//
//    @Test
//    fun testSetDarkMode() = runBlocking{
//        val darkModeValue = true
//        settingsRepository.setDarkMode(darkModeValue)
//
//        val updatedSettings = settingsStore.data.first()
//        assertEquals(darkModeValue, updatedSettings.darkMode)
//    }
//    @Test
//    fun testSerialization() = runBlocking{
//        val settingsSerializer = SettingsSerializer()
//        val testValue = 42.0
//        val originalSettings = Settings.newBuilder().setTest(testValue).build()
//        val outputStream = ByteArrayOutputStream()
//        settingsSerializer.writeTo(originalSettings, outputStream)
//        val serializedSettings = outputStream.toByteArray()
//
//        val inputStream = ByteArrayInputStream(serializedSettings)
//        val deserializedSettings = settingsSerializer.readFrom(inputStream)
//
//        assertEquals(originalSettings, deserializedSettings)
//
//    }
//
//    @Test
//    fun testAddFavorite() = runBlocking {
//        val favorite = "HODDEVIK"
//        settingsRepository.addFavoriteSurfArea(favorite)
//
//        val updatedSettings = settingsStore.data.first()
//        assertTrue(updatedSettings.favoriteSurfAreasList.contains(favorite))
//    }
//}
//
