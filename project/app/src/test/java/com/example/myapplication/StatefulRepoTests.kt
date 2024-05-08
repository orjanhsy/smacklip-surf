package com.example.myapplication

import androidx.compose.runtime.collectAsState
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastDataSource
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.home.HomeScreenViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class StatefulRepoTests {

    private val repo: Repository = RepositoryImpl()

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
    fun alertsAreX(): Unit = runBlocking{
        repo.loadAlerts()

        val state = repo.alerts.value
        println(state)
    }

    @Test
    fun pointForecastsFilterTime()= runBlocking{

        val time1 = measureTimeMillis {
            WaveForecastRepositoryImpl().allWavePeriodsNext3Days()
        }


        println("$time1")

    }

    @Test
    fun waveForecastXd(): Unit = runBlocking{
        WaveForecastRepositoryImpl().getAllWavePeriods().wavePeriods.entries.forEach {
            println("${it.key} -> ${it.value}")
        }
    }
}