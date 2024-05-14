package com.example.myapplication.data.oceanforecast

import android.util.Log
import com.example.myapplication.utils.HTTPServiceHandler.API_HEADER
import com.example.myapplication.utils.HTTPServiceHandler.API_KEY
import com.example.myapplication.utils.HTTPServiceHandler.OCEAN_FORECAST_URL
import com.example.myapplication.model.oceanforecast.OceanForecast
import com.example.myapplication.model.surfareas.SurfArea
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

private const val TAG = "OFDS"
public class OceanforecastDataSource(
    private val path: String = OCEAN_FORECAST_URL
) {

    private val client = HttpClient() {
        defaultRequest {
            url(OCEAN_FORECAST_URL)
            headers.appendIfNameAbsent(API_KEY, API_HEADER)
        }
        install(ContentNegotiation){
            gson()
        }
    }

    suspend fun fetchOceanforecast(surfArea: SurfArea): OceanForecast {
        val oceanForecast: OceanForecast = try {
            client.get("$path?lat=${surfArea.lat}&lon=${surfArea.lon}"){
                header(
                    API_HEADER, API_KEY
                )
            }.body()
        } catch (e: Exception) {
            // Does not handle http exceptions differently
            Log.e(TAG, "Failed to GET data. ${e.stackTraceToString()}")
            throw e
        }
        return oceanForecast
    }
}