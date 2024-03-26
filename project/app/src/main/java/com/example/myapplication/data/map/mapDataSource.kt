package com.example.myapplication.data.map

import com.example.myapplication.data.helpers.HTTPServiceHandler
import com.example.myapplication.data.helpers.HTTPServiceHandler.NORWAY_URL
import com.example.myapplication.model.oceanforecast.OceanForecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

class mapDataSource (
    private val path: String = "https://geojson.io/#map=4.26/64.8/12.94"
){
    private val client = HttpClient() {
        defaultRequest {
            url(NORWAY_URL)
            headers.appendIfNameAbsent(HTTPServiceHandler.API_KEY, HTTPServiceHandler.API_HEADER)
        }
        install(ContentNegotiation) {
            gson()
        }
    }


}