package com.example.myapplication

import com.example.myapplication.data.metalerts.MetAlertsDataSource
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FinalTests {

    //metalerts
    val metAlertsRepository = MetAlertsRepositoryImpl()
    val metAlertsDataSource = MetAlertsDataSource()

    @Test
    fun metAlertsDataSourceHandlesNon200() = runBlocking {

    }


}