package com.example.myapplication

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.surfarea.SurfAreaScreenViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.Thread.yield

//class ViewModelTests {
//
//    val savm = SurfAreaScreenViewModel()
//
//    @OptIn(ExperimentalCoroutinesApi::class)
////    @Test
////    fun conditionStatusesForFirst3DaysAreSameLengthAsForecastFirst3Days()= runBlocking{
////        savm.updateForecastNext7Days(SurfArea.HODDEVIK)
////        savm.updateConditionStatuses(SurfArea.HODDEVIK, savm.surfAreaScreenUiState.value.forecast7Days)
////
////        delay(20000)
////        for (day in 0 ..< savm.surfAreaScreenUiState.value.conditionStatuses.size ) {
////            println("Forecast: ${savm.surfAreaScreenUiState.value.forecast7Days[day]}")
////            println("Conditions: ${savm.surfAreaScreenUiState.value.conditionStatuses[day]}")
////            assert(savm.surfAreaScreenUiState.value.forecast7Days[day].size == savm.surfAreaScreenUiState.value.conditionStatuses[day]!!.size)
////        }
////    }
//}