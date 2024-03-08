package com.example.myapplication.data.locationForecast

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
import io.ktor.client.request.get
import io.ktor.client.call.body
import com.example.myapplication.model.locationforecast.LocationForecast


class LocationForecastDataSource {
    private val path: String = "https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=62.1255693551118&lon=5.152407834229069"

    private val client = HttpClient(){
        install(ContentNegotiation){
            gson()
        }
    }
    suspend fun fetchLocationForecastData(): LocationForecast {
        val response = client.get(path)
        return response.body()
    }
}