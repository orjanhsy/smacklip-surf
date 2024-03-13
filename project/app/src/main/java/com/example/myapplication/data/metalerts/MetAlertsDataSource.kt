package com.example.myapplication.data.metalerts

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
            url("gw-uio.intark.uh-it.no/in2000/weatherapi/weatherapi/metalerts/2.0/current.json")
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "f25d12be-cbd6-4adf-9aed-c41d84494cdb")
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
