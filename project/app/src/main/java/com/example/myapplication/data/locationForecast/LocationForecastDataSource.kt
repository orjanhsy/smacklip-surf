package com.example.myapplication.data.locationForecast

import com.example.myapplication.data.helpers.HTTPServiceHandler.API_HEADER
import com.example.myapplication.data.helpers.HTTPServiceHandler.API_KEY
import com.example.myapplication.data.helpers.HTTPServiceHandler.LOCATION_FORECAST_URL
import com.example.myapplication.model.locationforecast.LocationForecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent


class LocationForecastDataSource(
    private val path: String = "https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=62.1255693551118&lon=5.152407834229069"

) {

    private val client = HttpClient(){
        defaultRequest {
            url(LOCATION_FORECAST_URL)
            headers.appendIfNameAbsent(API_KEY, API_HEADER)
        }
        install(ContentNegotiation){
            gson()
        }
    }
    suspend fun fetchLocationForecastData(): LocationForecast {
        val locationForecast: LocationForecast = client.get(path).body()
        return locationForecast
    }
}