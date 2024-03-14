package com.example.myapplication.data.oceanforecast

import com.example.myapplication.model.oceanforecast.OceanForecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent

public class OceanforecastDataSource(
    private val path: String = "https://api.met.no/weatherapi/oceanforecast/2.0/complete?lat=62.1255693551118&lon=5.152407834229069"
) {

    private val client = HttpClient() {
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/weatherapi/oceanforecast/2.0/complete?lat=62.1255693551118&lon=5.152407834229069")
            headers.appendIfNameAbsent("X-Gravitee-API-Key", "f25d12be-cbd6-4adf-9aed-c41d84494cdb")
        }
        install(ContentNegotiation){
            gson()
        }
    }

    suspend fun fetchOceanforecast(): OceanForecast {
        val oceanForecast: OceanForecast = client.get(path).body()
        return oceanForecast
    }
}