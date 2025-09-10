package com.github.mikeandv.testassignment.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientHelper {
    private val authorizedClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                isLenient = true
            })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = AppPropsHelper.props.host
                encodedPath = AppPropsHelper.props.encodedPath
            }

            headers.append("X-Customer-Code", AppPropsHelper.props.xCustomerCode)
            headers.append("X-Api-Key", AppPropsHelper.props.xApiKey)
        }
    }
    private val unauthorizedClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = AppPropsHelper.props.host
                encodedPath = AppPropsHelper.props.encodedPath
            }
            headers.append("X-Customer-Code", AppPropsHelper.props.xCustomerCode)
        }
    }


    fun getAuthorizedClient(): HttpClient {
        return authorizedClient
    }

    fun getUnauthorizedClient(): HttpClient {
        return unauthorizedClient
    }

}