package no.uio.ifi.in2000.team8

import com.example.myapplication.R
import com.example.myapplication.data.weatherforecast.WeatherForecastRepositoryImpl
import com.example.myapplication.utils.DateUtils
import com.example.myapplication.utils.ResourceUtils
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UtilTests {

    @Test
    fun symbolCodeIsInResourceUtils(): Unit = runBlocking{
        val repo = WeatherForecastRepositoryImpl()
        repo.loadOFlF()
        val resourceUtils = ResourceUtils()
        val ofLfForecast = repo.ofLfForecast.value
        ofLfForecast.next7Days.values.map {week ->
            week.forecast.map {day ->
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