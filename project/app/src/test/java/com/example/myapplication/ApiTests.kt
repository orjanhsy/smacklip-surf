package com.example.myapplication


import com.example.myapplication.data.locationforecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsDataSource
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanForecastRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastDataSource
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.oceanforecast.OceanForecast
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.model.surfareas.SurfArea
import com.google.gson.Gson
import com.mapbox.maps.extension.style.expressions.dsl.generated.any
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
class ApiTests {

    //global

    //WaveForecast
    private val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl()
    private val waveForecastDataSource: WaveForecastDataSource = WaveForecastDataSource()





    //MetAlerts
    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl()
    private val metAlertsDataSource: MetAlertsDataSource = MetAlertsDataSource()

    @Test
    fun metAlertsDataSourceThrowsNon200() = runBlocking {
        val metAlertsDataSource = MetAlertsDataSource("error_url")
        val response = try { metAlertsDataSource.fetchMetAlertsData() }
        catch (e: Exception) {
            null
        }
        assert(response==null)
    }

    @Test
    fun ifNameIsInAlertItIsRelevant()= runBlocking {
        metAlertsRepository.loadAllRelevantAlerts()
        val allAlerts = metAlertsDataSource.fetchMetAlertsData()
        val relevantAlerts: Map<SurfArea, List<Alert>> = metAlertsRepository.alerts.value
        SurfArea.entries.forEach {
            if (relevantAlerts[it]?.isNotEmpty() == true) {
                assert(it.locationName !in allAlerts.features.map {alert -> alert.properties?.area })
            }
        }
    }


    //Ocean forecast
    private val oceanForecastRepository = OceanForecastRepositoryImpl()




    //Location Forecast
    private val locationForecastRepository = LocationForecastRepositoryImpl()




}

