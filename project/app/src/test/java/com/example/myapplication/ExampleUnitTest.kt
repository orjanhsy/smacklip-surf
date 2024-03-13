package com.example.myapplication



import com.example.myapplication.data.metalerts.MetAlertsRepository
import com.example.myapplication.model.metalerts.MetAlerts
import com.google.gson.Gson
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import kotlinx.coroutines.runBlocking
import com.example.myapplication.data.oceanforecast.HoddevikDataSourceDataSource
import com.example.myapplication.data.oceanforecast.HoddevikRepository
import com.example.myapplication.model.SurfArea
import com.example.myapplication.model.metalerts.Features

import com.example.myapplication.model.oceanforecast.Data
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

import kotlinx.coroutines.test.runTest

import org.junit.Test
import org.junit.Assert.*
import java.io.File

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
        val features = async {metAlertsRepository.getFeatures()}
        val relevantAlerts = metAlertsRepository.getRelevantAlertsFor(SurfArea.FEDJE, features.await())
        println(relevantAlerts)
        relevantAlerts.forEach { println("Alert: $it") }
    }

    @Test
    fun getRelevantAlertsForNordkappOnlyGetsAlertsFromAreaNordkapp() {
        println("Script is ran from: ${System.getProperty("user.dir")}")
        val metAlerts: MetAlerts = gson.fromJson(metAlertJson, MetAlerts::class.java)
        val features = metAlerts.features
        val relevantAlerts = metAlertsRepository.getRelevantAlertsFor(SurfArea.NORDKAPP, features)
        relevantAlerts.forEach { assert(it.properties?.area?.lowercase()?.contains("nordkapp") == true) }
    }


    //Ocean forecast
    private val hoddevikDataSourceDataSource = HoddevikDataSourceDataSource()
    private val hoddevikRepository = HoddevikRepository(hoddevikDataSourceDataSource)

    //Met alerts
    private val repo = MetAlertsRepositoryImpl()
    
    //LF
    private val locationForecastDataSource = LocationForecastDataSource()
    private val locationForecastRepository = LocationForecastRepository(locationForecastDataSource)
    private val hoddevikDataSourceDataSource = HoddevikDataSourceDataSource()
    private val hoddevikRepository = HoddevikRepository(hoddevikDataSourceDataSource)

    @Test
    fun locationForecastTimeSeriesExists() = runBlocking {
        val timeSeries: List<Pair<String, DataLF>> = locationForecastRepository.getTimeSeries()
        val time1 = timeSeries.get(0).first

        print("$time1 ----------Testen fungerer!----------")


    }

    @Test
    fun oceanForecastTimeSeriesExists() = runBlocking {

        val timeSeries: List<Pair<String, Data>> = hoddevikRepository.getTimeSeries()
        val time1 = timeSeries.get(0).first
        //val data1 = timeSeries.get(0).second
        //assertEquals("2024-03-07T13:00:00Z", time1)
        println("$time1 --------------hei----------------")

    }

}