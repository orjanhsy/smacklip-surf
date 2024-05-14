package no.uio.ifi.in2000.team8.data.locationforecast

import android.util.Log
import no.uio.ifi.in2000.team8.utils.HTTPServiceHandler.API_HEADER
import no.uio.ifi.in2000.team8.utils.HTTPServiceHandler.API_KEY
import no.uio.ifi.in2000.team8.utils.HTTPServiceHandler.LOCATION_FORECAST_URL
import no.uio.ifi.in2000.team8.model.locationforecast.LocationForecast
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
        } catch (e: Exception) {
            Log.e(TAG, "Failed to GET data ${e.stackTraceToString()}")
            throw e
        }

        return locationForecast
    }
}