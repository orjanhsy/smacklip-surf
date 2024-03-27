package com.example.myapplication.data.waveforecast

import com.example.myapplication.data.helpers.HTTPServiceHandler
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

class WaveForecastDataSource {

    private val waveForecastUrl = "https://api.met.no/weatherapi/metalerts/2.0/current.json"

    private val client = HttpClient {
        defaultRequest {
            url(HTTPServiceHandler.METALERTS_URL)
            headers.appendIfNameAbsent(HTTPServiceHandler.API_KEY, HTTPServiceHandler.API_HEADER)
        }
        install(ContentNegotiation) {
            gson()
        }
    }
}