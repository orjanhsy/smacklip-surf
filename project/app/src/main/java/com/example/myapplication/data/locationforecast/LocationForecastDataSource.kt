package com.example.myapplication.data.locationforecast

import android.util.Log
import com.example.myapplication.utils.HTTPServiceHandler.API_HEADER
import com.example.myapplication.utils.HTTPServiceHandler.API_KEY
import com.example.myapplication.utils.HTTPServiceHandler.LOCATION_FORECAST_URL
import com.example.myapplication.model.locationforecast.LocationForecast
import com.example.myapplication.model.surfareas.SurfArea
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
private const val TAG = "LFDS"
class LocationForecastDataSource(
    private val locationForecastUrl: String = LOCATION_FORECAST_URL

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

    suspend fun fetchLocationForecastData(surfArea: SurfArea): LocationForecast {
        val locationForecast: LocationForecast = try {
            client.get(
                "$locationForecastUrl?lat=${surfArea.lat}&lon=${surfArea.lon}"
            ) {
                header(API_HEADER, API_KEY)
            }.body()
        }
        catch(e: RedirectResponseException) {
            // 3xx
            Log.e(TAG, "Failed get data for $surfArea. 3xx-error. Cause: ${e.message}")
            throw e
        }
        catch (e: ClientRequestException) {
            // 4xx
            Log.e(TAG, "Failed get data for $surfArea. 4xx-error. Cause: ${e.message}")
            throw e
        }
        catch(e: ServerResponseException) {
            // 5xx
            Log.e(TAG, "Failed get data for $surfArea. 5xx-error. Cause: ${e.message}")
            throw e
        }
        catch (e: Exception) {
            Log.e(TAG, "Failed get data for $surfArea. Unknown error. Cause: ${e.message}")
            throw e
        }

        return locationForecast
    }
}