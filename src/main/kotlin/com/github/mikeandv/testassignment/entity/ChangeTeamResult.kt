package com.github.mikeandv.testassignment.entity

import io.ktor.http.HttpStatusCode

data class ChangeTeamResult(
    val status: HttpStatusCode,
    val licenses: List<LicenseResponse>,
    val fromTeam: LicenseResponse.TeamResponse,
    val toTeam: LicenseResponse.TeamResponse,
)