package com.github.mikeandv.testassignment.utils

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.ChangeTeamRequest
import com.github.mikeandv.testassignment.entity.LicenseResponseError
import com.github.mikeandv.testassignment.entity.LicenseResponse
import io.ktor.http.HttpStatusCode

data class AssignLicenseSuccessCase(
    val valueDesc: String,
    val payloadFunc: suspend () -> Pair<String, AssignLicenseRequest>,
    val expectedUser: AppPropsHelper.AppProps.User
)
data class ChangeTeamSuccessCase(
    val valueDesc: String,
    val payloadHelperFunc: suspend () -> List<LicenseResponse.TeamResponse>,
    val payloadFunc: suspend (Int, Int) -> Pair<List<String>, ChangeTeamRequest>,
)

data class ErrorAndStatusCase(
    val valueDesc: String,
    val path: String,
    val payload: Any,
    val expectedStatus: HttpStatusCode,
    val expectedError: LicenseResponseError
)

data class AssignLicenseErrorExtendedCase(
    val valueDesc: String,
    val payloadFunc: suspend () -> Pair<String, AssignLicenseRequest>,
    val expectedStatus: HttpStatusCode,
    val expectedError: LicenseResponseError,
)

data class StatusOnlyCase(
    val valueDesc: String,
    val path: String,
    val payload: Any,
    val expectedStatus: HttpStatusCode
)

data class StatusAndBodyCase(
    val valueDesc: String,
    val path: String,
    val payload: Any,
    val expectedStatus: HttpStatusCode,
    val expectedBody: String
)
