package com.example.myapplication.data.locationForecast

import android.util.Log
import com.example.myapplication.data.helpers.HTTPServiceHandler.API_HEADER
import com.example.myapplication.data.helpers.HTTPServiceHandler.API_KEY
import com.example.myapplication.data.helpers.HTTPServiceHandler.LOCATION_FORECAST_URL
import com.example.myapplication.model.locationforecast.LocationForecast
import com.example.myapplication.model.surfareas.SurfArea
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
    private val TAG = "LocationForecastDataSource"


    private val client = HttpClient(){
        defaultRequest {
            url(LOCATION_FORECAST_URL)
            headers.appendIfNameAbsent(API_KEY, API_HEADER)
        }
        install(ContentNegotiation){
            gson()
        }
    }
    suspend fun fetchLocationForecastData(surfArea: SurfArea): LocationForecast? {
        //Log.d(TAG, "Fetching forecast data for SurfArea: ${surfArea.locationName}, lat: ${surfArea.lat}, lon: ${surfArea.lon}")
        val locationUrl = "$path?lat=${surfArea.lat}&lon=${surfArea.lon}"
        val locationForecast: LocationForecast? = client.get(locationUrl).body()
        //Log.d(TAG, "Raw Response: $locationForecast" )
        //Log.d(TAG, "Response for ${surfArea.locationName} forecast: $locationForecast")
        val windDirection = locationForecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.wind_from_direction
        val windSpeed = locationForecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.wind_speed
        val windSpeedOfGust = locationForecast?.properties?.timeseries?.firstOrNull()?.data?.instant?.details?.wind_speed_of_gust

        //Log.d(TAG, "Wind Direction: $windDirection")
        //Log.d(TAG, "Wind Speed: $windSpeed")
        //Log.d(TAG, "Wind Speed of Gust: $windSpeedOfGust")
        return locationForecast
    }
}