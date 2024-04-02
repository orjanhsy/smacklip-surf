package com.example.myapplication.data.locationForecast

import com.example.myapplication.data.helpers.HTTPServiceHandler.API_HEADER
import com.example.myapplication.data.helpers.HTTPServiceHandler.API_KEY
import com.example.myapplication.data.helpers.HTTPServiceHandler.LOCATION_FORECAST_URL
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.locationforecast.LocationForecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent


class LocationForecastDataSource(
    private val path: String = LOCATION_FORECAST_URL

) {

    private val client = HttpClient(){
        defaultRequest {
            url(path)
            headers.appendIfNameAbsent(API_KEY, API_HEADER)
        }
        install(ContentNegotiation){
            gson()
        }
    }
    suspend fun fetchLocationForecastData(surfArea: SurfArea): LocationForecast {
        val lf = "$path?lat=${surfArea.lat}&lon=${surfArea.lon}"
        return client.get(lf).body()
    }
}