package com.example.myapplication.data.metalerts

import android.net.http.HttpException
import android.util.Log
import com.example.myapplication.data.utils.HTTPServiceHandler.API_HEADER
import com.example.myapplication.data.utils.HTTPServiceHandler.API_KEY
import com.example.myapplication.data.utils.HTTPServiceHandler.METALERTS_URL
import com.example.myapplication.model.metalerts.MetAlerts
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

private const val TAG = "MetAlertsDS"

class MetAlertsDataSource(private val metAlertsUrl: String = METALERTS_URL) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }

    }


    suspend fun fetchMetAlertsData(): MetAlerts {
        val response = try {client.get(metAlertsUrl)}
        catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            throw e
        }
        return response.body()
    }
}
