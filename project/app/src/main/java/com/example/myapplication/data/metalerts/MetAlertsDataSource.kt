package com.example.myapplication.data.metalerts

import com.example.myapplication.data.helpers.HTTPServiceHandler.API_HEADER
import com.example.myapplication.data.helpers.HTTPServiceHandler.API_KEY
import com.example.myapplication.data.helpers.HTTPServiceHandler.METALERTS_URL
import com.example.myapplication.model.metalerts.MetAlerts
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

class MetAlertsDataSource {
    private val metAlertsCurrentUrl = "https://api.met.no/weatherapi/metalerts/2.0/current.json"

    private val client = HttpClient {
        defaultRequest {
            url(METALERTS_URL)
            headers.appendIfNameAbsent(API_KEY, API_HEADER)
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchMetAlertsData(): MetAlerts {
        val response = client.get(metAlertsCurrentUrl)
        return response.body()
    }
}
