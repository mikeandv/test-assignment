package com.github.mikeandv.testassignment.utils

import com.github.mikeandv.testassignment.entity.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

object HttpRequestHelper {

    suspend fun HttpClient.postAndReturnLicense(payloadFunc: suspend () -> Pair<String, AssignLicenseRequest>): Pair<HttpStatusCode, LicenseResponse> {
        val payload = payloadFunc()
        val response = this.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(payload.second)
        }

        val license = TestDataHelper.getLicenseResponseByLicenseId(payload.first)
        return response.status to license
    }

    suspend fun HttpClient.postAndReturnLicense(
        payloadHelperFunc: suspend () -> List<LicenseResponse.TeamResponse>,
        payloadFunc: suspend (Int, Int) -> Pair<List<String>, ChangeTeamRequest>,
    ): ChangeTeamResult {
        val (fromTeam, toTeam) = payloadHelperFunc()
        val (licenseIds, request) = payloadFunc(fromTeam.id, toTeam.id)

        val response = this.post(AppPropsHelper.props.customerChangeLicensesTeam) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val licenses = licenseIds.map { id ->
            TestDataHelper.getLicenseResponseByLicenseId(id)
        }

        return ChangeTeamResult(response.status, licenses, fromTeam, toTeam)
    }


    suspend fun HttpClient.postAndReturnError(payload: Any, path: String): Pair<HttpStatusCode, LicenseResponseError> {
        val response = this.post(path) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        val error = Json.decodeFromString<LicenseResponseError>(response.bodyAsText())
        return response.status to error
    }

    suspend fun HttpClient.postAndReturnError(payloadFunc: suspend () -> Pair<String, AssignLicenseRequest>): Pair<HttpStatusCode, LicenseResponseError> {
        val payload = payloadFunc()
        val response = this.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(payload.second)
        }

        val error = Json.decodeFromString<LicenseResponseError>(response.bodyAsText())
        return response.status to error
    }

    suspend fun HttpClient.postAndReturnStatus(payload: Any, path: String): HttpStatusCode {
        val response = this.post(path) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return response.status
    }

    suspend fun HttpClient.postAndReturnBody(payload: Any, path: String): Pair<HttpStatusCode, String> {
        val response = this.post(path) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        val responseBody: String = response.body()
        return response.status to responseBody
    }
}