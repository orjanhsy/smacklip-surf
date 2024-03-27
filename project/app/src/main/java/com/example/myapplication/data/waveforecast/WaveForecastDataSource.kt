package com.example.myapplication.data.waveforecast

import com.example.myapplication.data.helpers.HTTPServiceHandler.API_HEADER
import com.example.myapplication.data.helpers.HTTPServiceHandler.API_KEY
import com.example.myapplication.data.helpers.HTTPServiceHandler.WAVE_FORECAST_BASE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.kotlinx.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.Serializable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
@Serializable
data class TokenResponse(val access_token: String, val token_type: String)

fun main() {
    defaultServer(Application::main).start()
    runBlocking {
        val client = HttpClient(CIO) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = "jetbrains", password = "foobar")
                    }
                    realm = "Access to the '/' path"
                }
            }
        }
        val response: HttpResponse = client.get("http://0.0.0.0:8080/")
        println(response.bodyAsText())
        client.close()
    }
}


class WaveForecastDataSource {

    private val client = HttpClient {
        defaultRequest {
            url(WAVE_FORECAST_BASE)
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchWaveForecastData(): Any {
        return client.get("/v1/geodata/waveforecast/pointforecast")
    }
}