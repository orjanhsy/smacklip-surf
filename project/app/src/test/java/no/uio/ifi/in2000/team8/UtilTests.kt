package no.uio.ifi.in2000.team8

import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepositoryImpl
import no.uio.ifi.in2000.team8.utils.DateUtils
import no.uio.ifi.in2000.team8.utils.ResourceUtils
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UtilTests {

    @Test
    fun symbolCodeIsInResourceUtils(): Unit = runBlocking{
        val repo = WeatherForecastRepositoryImpl()
        repo.loadOFlF()
        val resourceUtils = ResourceUtils()
        val ofLfForecast = repo.ofLfForecast.value
        ofLfForecast.forecasts.values.map { week ->
            week.dayForecasts.map { day ->
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