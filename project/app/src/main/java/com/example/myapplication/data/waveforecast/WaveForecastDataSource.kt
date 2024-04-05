package com.example.myapplication.data.waveforecast

import com.example.myapplication.config.Client
import com.example.myapplication.config.Config
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
import io.ktor.client.request.forms.submitForm
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json

fun main() {

}


class WaveForecastDataSource {

    private val tokenClient = HttpClient {
        expectSuccess = true

        install(ContentNegotiation) {
            gson()
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, request ->
                val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                val exceptionResponse = clientException.response
                if (exceptionResponse.status == HttpStatusCode.BadRequest) {
                    val exceptionResponseText = exceptionResponse.bodyAsText()
                    throw ClientRequestException(exceptionResponse, exceptionResponseText)
                }
            }
        }
    }

    private val bearerTokenStorage = mutableListOf<BearerTokens>()

    val client = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {

                loadTokens {
                    bearerTokenStorage.last()
                }

                //vil hente en ny token når token hentet fra loadTokens resulterer i 401 (unauthorized)
                refreshTokens {
                    val refreshToken: AccessToken = client.submitForm(
                        url = "https://id.barentswatch.no/connect/token",
                        formParameters = parameters {
                            append("grant_type", "client_credentials")
                            append("client_id", Config.CLIENT_ID)
                            append("client_secret", Config.CLIENT_SECRET)
                            append("scope", "api")
                            append("refresh_token", oldTokens?.refreshToken ?: "")
                        }
                    ) {markAsRefreshTokenRequest()}.body<AccessToken>()
                    bearerTokenStorage.add(BearerTokens(refreshToken.accessToken, oldTokens?.refreshToken!!))
                    bearerTokenStorage.last()
                }

//                sendWithoutRequest {
//                    it.url.host == ""
//                }

            }
        }
    }

    suspend fun getTokenAccess(): Pair<String, String> {
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