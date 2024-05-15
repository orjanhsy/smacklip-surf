package no.uio.ifi.in2000.team8


import com.example.myapplication.data.metalerts.MetAlertsDataSource
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.data.weatherforecast.WeatherForecastRepositoryImpl
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ApiAndParsingTests {


    //WaveForecast
    private val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl()

    @Test
    fun waveForecastIsBetween57And63HoursLong()= runBlocking{
        val allForecasts = waveForecastRepository.getAllWavePeriods()
        allForecasts.wavePeriods.entries.forEach { (sa, forecast) ->
            val forecastNumber = forecast.entries.sumOf { it.value.size }
            assert(forecastNumber in 57 .. 63)
        }
    }


    //MetAlerts
    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl()
    private val metAlertsDataSource: MetAlertsDataSource = MetAlertsDataSource()

    @Test
    fun locationNameIsNotInAnyAlertIfRelevantAlertsIsEmptyForThatArea()= runBlocking {
        metAlertsRepository.loadAllRelevantAlerts()
        val allAlerts = metAlertsDataSource.fetchMetAlertsData()
        println(allAlerts)
        val relevantAlerts: Map<SurfArea, List<Alert>> = metAlertsRepository.alerts.value
        println(relevantAlerts)

        SurfArea.entries.forEach {
            if (relevantAlerts[it]?.isEmpty() == true) {
                assert(it.locationName !in allAlerts.features.map {alert -> alert.properties?.area })
            }
        }
    }

    private val weatherRepo = WeatherForecastRepositoryImpl()
    @Test
    fun sizeOfOneDayForecastIsInRange1To24(): Unit = runBlocking {
        weatherRepo.loadOFlF()
        val state = weatherRepo.ofLfForecast.value
        state.next7Days.values.map{
            it.forecast.map {day ->
                assert(day.data.size in 1..24)
            }
        }
    }


}

