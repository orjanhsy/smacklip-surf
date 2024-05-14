package no.uio.ifi.in2000.team8

import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepository
import no.uio.ifi.in2000.team8.data.weatherforecast.WeatherForecastRepositoryImpl
import no.uio.ifi.in2000.team8.data.waveforecast.WaveForecastRepositoryImpl
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import kotlinx.coroutines.runBlocking
import org.junit.Test

class StatefulRepoTests {

    private val repo: WeatherForecastRepository = WeatherForecastRepositoryImpl()

    @Test
    fun daysOfAvaliabelForecastIsX(): Unit = runBlocking{
        repo.loadOFlF()
        val state = repo.ofLfNext7Days.value
        state.next7Days.entries.map {
            println("${it.key} -> ${it.value.forecast[it.value.forecast.size]}")
        }
    }

    @Test
    fun sizeOfOneDayForecastIsInRangeXY(): Unit = runBlocking {
        repo.loadOFlF()
        val state = repo.ofLfNext7Days.value
        state.next7Days[SurfArea.HODDEVIK]!!.forecast.map {
            println(it.data.size)
        }
    }

    @Test
    fun wavePeriodsAreX(): Unit = runBlocking{
        repo.loadWavePeriods()
        val state = repo.wavePeriods.value
        state.wavePeriods.entries.map {
            println(it)
        }
    }



    @Test
    fun waveForecastXd(): Unit = runBlocking{
        WaveForecastRepositoryImpl().getAllWavePeriods().wavePeriods.entries.forEach {
            println("${it.key} -> ${it.value}")
        }
    }
}