package com.example.myapplication.data.waveforecast

import com.example.myapplication.config.Client
import com.example.myapplication.data.helpers.HTTPServiceHandler.WAVE_FORECAST_BASE
import com.example.myapplication.data.helpers.HTTPServiceHandler.WAVE_FORECAST_POINT_FORECAST
import com.example.myapplication.data.helpers.HTTPServiceHandler.WF_AVALIABLE_ALL
import com.example.myapplication.data.helpers.HTTPServiceHandler.WF_TEST_URL
import com.example.myapplication.model.waveforecast.AccessToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders.Accept
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json

fun main() {

}

/* TODO:
1. get 200 response for
2. serialize response
3. create a way to refresh tokens
 */
class WaveForecastDataSource {

    private val tokenClient = HttpClient {
        expectSuccess = true

        install(ContentNegotiation) {
            gson()
        }
    }

    private val bearerTokenStorage = mutableListOf<BearerTokens>()

    private val client = HttpClient() {
        install(Logging)
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {

                loadTokens {
                     bearerTokenStorage.last()
                }

            }
        }

    }
    suspend fun fetchPointForecast(): HttpResponse {
        if (bearerTokenStorage.isEmpty()){
            val (token, refresh) = getTokenAccess()
            bearerTokenStorage.add(BearerTokens(token, ""))
        }
        return try {
            val response: HttpResponse = client.get(WF_AVALIABLE_ALL) {
            }
            response
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getTokenAccess(): Pair<String, String?> {
        val requestBody = parameters {
            append("grant_type", "client_credentials")
            append("client_id", Client.CLIENT_ID)
            append("client_secret", Client.CLIENT_SECRET)
            append("scope", "api")
        }
        val accessToken = tokenClient.post("https://id.barentswatch.no/connect/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(requestBody.formUrlEncode())
        }
        return Pair(accessToken.body<AccessToken>().accessToken, accessToken.body<AccessToken>().refreshToken)
    }
}