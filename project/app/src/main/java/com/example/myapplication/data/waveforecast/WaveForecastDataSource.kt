package com.example.myapplication.data.waveforecast

import com.example.myapplication.data.config.Client
import com.example.myapplication.data.utils.HTTPServiceHandler.WF_ACCESS_TOKEN_URL
import com.example.myapplication.data.utils.HTTPServiceHandler.WF_BASE_URL
import com.example.myapplication.data.utils.HTTPServiceHandler.WF_CLOSEST_ALL_TIME_URL
import com.example.myapplication.model.waveforecast.AccessToken
import com.example.myapplication.model.waveforecast.NewPointForecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson

fun main() {

}

/* TODO:
create a way to refresh tokens (redirect?), bw do not provide refresh-tokens
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


    suspend fun fetchNearestPointForecast(lat: Double, lon: Double): List<NewPointForecast> {
        if (bearerTokenStorage.isEmpty()){
            val (token, _) = getAccessToken()
            bearerTokenStorage.add(BearerTokens(token, ""))
        }
        return try {
            val response = client.get("$WF_CLOSEST_ALL_TIME_URL?x=$lon&y=$lat")
            response.body()
        } catch (e: Exception) {
            throw e
            /* TODO: Handle exceptions appropriately
            - parameter is invalid (?)
            - token is expired (401)
            - other error (500)
             */
        }
    }



    private suspend fun getAccessToken(): Pair<String, String?> {
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