package com.example.myapplication.data.waveforecast

import com.example.myapplication.config.Client
import com.example.myapplication.data.helpers.HTTPServiceHandler.WF_ACCESS_TOKEN_URL
import com.example.myapplication.data.helpers.HTTPServiceHandler.WF_ALL_POINT_FORECASTS_URL
import com.example.myapplication.data.helpers.HTTPServiceHandler.WF_AVALIABLE_ALL_URL
import com.example.myapplication.data.helpers.HTTPServiceHandler.WF_BASE_URL
import com.example.myapplication.model.waveforecast.AccessToken
import com.example.myapplication.model.waveforecast.PointForecast
import com.example.myapplication.model.waveforecast.PointForecasts
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.HttpHeaders.Accept
import io.ktor.serialization.gson.gson
import io.ktor.serialization.jackson.jackson
import io.ktor.serialization.kotlinx.json.json

fun main() {

}

/* TODO:
1. get 200 response necessary 200 response
2. serialize response
3. create a way to refresh tokens (redirect?)
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
        install(DefaultRequest) {
            url(WF_BASE_URL)
        }
        install(Logging)
        install(ContentNegotiation) {
            gson()
        }
        install(Auth) {
            bearer {
                loadTokens {
                     bearerTokenStorage.last()
                }
            }
        }

    }
    suspend fun fetchPointForecast(): Array<PointForecast> {
        if (bearerTokenStorage.isEmpty()){
            val (token, refresh) = getAccessToken()
            bearerTokenStorage.add(BearerTokens(token, ""))
        }
        return try {
            val response = client.get(WF_ALL_POINT_FORECASTS_URL) {
            }
            response.body<Array<PointForecast>>()
        } catch (e: Exception) {
            throw e //lol
        }
    }

    suspend fun getAccessToken(): Pair<String, String?> {
        val requestBody = parameters {
            append("grant_type", "client_credentials")
            append("client_id", Client.CLIENT_ID)
            append("client_secret", Client.CLIENT_SECRET)
            append("scope", "api")
        }
        val accessToken = tokenClient.post(WF_ACCESS_TOKEN_URL) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(requestBody.formUrlEncode())
        }
        return Pair(accessToken.body<AccessToken>().accessToken, accessToken.body<AccessToken>().refreshToken)
    }
}