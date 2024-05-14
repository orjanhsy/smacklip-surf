package com.example.myapplication.data.metalerts

import android.util.Log
import com.example.myapplication.utils.HTTPServiceHandler.METALERTS_URL
import com.example.myapplication.model.metalerts.MetAlerts
import com.example.myapplication.utils.HTTPServiceHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

private const val TAG = "MetAlertsDS"

class MetAlertsDataSource(private val metAlertsUrl: String = METALERTS_URL) {

    private val client = HttpClient {
        defaultRequest {
            url(metAlertsUrl)
            headers.appendIfNameAbsent(HTTPServiceHandler.API_KEY, HTTPServiceHandler.API_HEADER)
        }
        install(ContentNegotiation) {
            gson()
        }
    }


    suspend fun fetchMetAlertsData(): MetAlerts {
        val response = try {
            client.get(metAlertsUrl)
        } catch (e: Exception) {
            // does not handle exceptions differently
            Log.e(TAG, "${e.message}")
            throw e
        }
        return response.body()
    }
}
