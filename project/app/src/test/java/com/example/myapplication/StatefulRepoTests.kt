package com.example.myapplication

import androidx.compose.runtime.collectAsState
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.home.HomeScreenViewModel
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
    fun wavePeriodsAreX() = runBlocking{
        repo.loadWavePeriods()
        val state = repo.wavePeriods.value
        println(state)
    }

    @Test
    fun alertsAreX() = runBlocking{
        repo.loadAlerts()

        val state = repo.alerts.value
        println(state)
    }

    // ViewModels
    @Test
    fun vmSate() = runBlocking{
        val hsvm = HomeScreenViewModel(RepositoryImpl())
        hsvm.updateOfLF()
        print(hsvm.homeScreenUiState.value.ofLfNow)
    }

}