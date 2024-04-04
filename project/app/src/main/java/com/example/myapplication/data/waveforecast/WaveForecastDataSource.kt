package com.example.myapplication.data.waveforecast

import com.example.myapplication.data.Config
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
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*

fun main() {

}


class WaveForecastDataSource {

    private val client = HttpClient {
        expectSuccess = true

        install(Logging)
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

    suspend fun getTokenAccess(): HttpResponse {
        val clientId = Config.CLIENT_ID
        val requestBody = Parameters.build {
            append("grant_type", "client_credentials",)
            append("client_id", clientId)
            append("client_secret", Config.CLIENT_SECRET)
            append("scope", "api")
        }
            val accessToken = client.post("https://id.barentswatch.no/connect/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(requestBody.formUrlEncode())
            }
            return accessToken
    }
}