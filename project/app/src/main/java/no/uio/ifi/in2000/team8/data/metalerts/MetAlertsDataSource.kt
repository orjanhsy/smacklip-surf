package com.example.myapplication.data.metalerts

import android.util.Log
import com.example.myapplication.utils.HTTPServiceHandler.METALERTS_URL
import com.example.myapplication.model.metalerts.MetAlerts
import com.example.myapplication.utils.HTTPServiceHandler
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

private const val TAG = "MetAlertsDS"

class MetAlertsDataSource(private val metAlertsUrl: String = METALERTS_URL) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }


    // gets all currently available alerts from MET
    suspend fun fetchMetAlertsData(): MetAlerts {
        return try {
            client.get(metAlertsUrl).body()
        }
        catch(e: RedirectResponseException) {
            // 3xx
            Log.e(TAG, "Failed get alerts. 3xx-error. Cause: ${e.message}")
            throw e
        }
        catch (e: ClientRequestException) {
            // 4xx
            Log.e(TAG, "Failed get alerts. 4xx-error. Cause: ${e.message}")
            throw e
        }
        catch(e: ServerResponseException) {
            // 5xx
            Log.e(TAG, "Failed get alerts. 5xx-error. Cause: ${e.message}")
            throw e
        }
        catch (e: Exception) {
            Log.e(TAG, "Failed get alerts. Unknown error. Cause: ${e.message}")
            throw e
        }
    }
}
