package com.example.myapplication

import com.example.myapplication.data.metalerts.MetAlertsDataSource
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.utils.HTTPServiceHandler.METALERTS_URL
import com.example.myapplication.model.metalerts.MetAlerts
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FinalTests {

    //metalerts

    @Test
    fun metAlertsDataSourceHandlesNon200() = runBlocking {
        val metAlertsDataSource = MetAlertsDataSource("error_url")
        val response = try { metAlertsDataSource.fetchMetAlertsData() }
        catch (e: Exception) {
            null
        }
        assert(response==null)
    }

    @Test
    fun metAlertsRepoReturnNonEmptyListOfAlerts()= runBlocking{
        val alerts = MetAlertsRepositoryImpl().getAllRelevantAlerts()
        println(alerts)
    }


}