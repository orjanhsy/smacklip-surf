package com.example.myapplication.data.waveforecast

import com.example.myapplication.data.Config
import com.example.myapplication.model.waveforecast.AccessToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.serialization.gson.gson

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

    private val client = HttpClient(CIO) {
        install(Auth) {
            bearer {
//                loadTokens {  }
//                refreshTokens {  }
            }
        }
    }

    suspend fun getTokenAccess(): String? {
        val requestBody = parameters {
            append("grant_type", "client_credentials")
            append("client_id", Config.CLIENT_ID)
            append("client_secret", Config.CLIENT_SECRET)
            append("scope", "api")
        }
        val accessToken = tokenClient.post("https://id.barentswatch.no/connect/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(requestBody.formUrlEncode())
        }
        return accessToken.body<AccessToken>().accessToken
    }
}