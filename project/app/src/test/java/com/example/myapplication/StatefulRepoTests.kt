package com.example.myapplication

import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.runBlocking
import org.junit.Test

class StatefulRepoTests {

    val repo: Repository = RepositoryImpl()

    @Test
    fun daysOfAvaliabelForecastIsX(): Unit = runBlocking{
        repo.loadOFlF()
        val state = repo.ofLfNext7Days.value
        state.next7Days.entries.map {
            println("${it.key} -> ${it.value.forecast[it.value.forecast.size]}")
        }
    }

    @Test
    fun sizeOfOneDayForecast(): Unit = runBlocking {
        repo.loadOFlF()
        val state = repo.ofLfNext7Days.value
        state.next7Days[SurfArea.HODDEVIK]!!.forecast.map {
            println(it.data.size)
        }
    }

}