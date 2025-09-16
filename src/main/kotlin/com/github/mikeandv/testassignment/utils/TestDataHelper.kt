package com.github.mikeandv.testassignment.utils

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.AssignLicenseRequest.AssignFromTeamRequest
import com.github.mikeandv.testassignment.entity.AssignLicenseRequest.AssigneeContactRequest
import com.github.mikeandv.testassignment.entity.ChangeTeamRequest
import com.github.mikeandv.testassignment.entity.LicenseResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

object TestDataHelper {
    private val client = HttpClientHelper.getAuthorizedClient()

    suspend fun getLicensesList(): List<LicenseResponse> {
        val response = client.get(AppPropsHelper.props.customerLicensesPath)
        return response.body()
    }

    suspend fun getLicensesByTeamId(teamId: Int): List<LicenseResponse> {
        val response =
            client.get(AppPropsHelper.props.customerTeamLicensesByTeamIdPath.replace("{teamId}", teamId.toString()))
        return response.body()
    }

    suspend fun getAllocatedAssignLicense(
        mainUser: AppPropsHelper.AppProps.User,
        secondUser: AppPropsHelper.AppProps.User
    ): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        val response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(
                AssignLicenseRequest(
                    contact = AssigneeContactRequest(
                        secondUser.email,
                        secondUser.firstName,
                        secondUser.lastName
                    ),
                    includeOfflineActivationCode = false,
                    license = null,
                    licenseId = license.licenseId,
                    sendEmail = false
                )
            )
        }
        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("Assign license to second user request failed with status ${response.status}")
        }
        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    mainUser.email,
                    mainUser.firstName,
                    mainUser.lastName
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }


    suspend fun getAssignLicenseRequestWithLicenseId(
        user: AppPropsHelper.AppProps.User
    ): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    user.email,
                    user.firstName,
                    user.lastName,
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getUnavailableToAssignLicense(user: AppPropsHelper.AppProps.User): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && !it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    user.email,
                    user.firstName,
                    user.lastName
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getUnavailableToAssignLicenseeFromTeam(user: AppPropsHelper.AppProps.User): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList()
            .groupBy { it.team }
            .entries.firstOrNull { item -> item.value.all { it.assignee == null && !it.isAvailableToAssign } }
            ?.value
            ?.firstOrNull() ?: throw IllegalStateException("No team with only unavailable to assign licenses")
        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    user.email,
                    user.firstName,
                    user.lastName
                ),
                false,
                AssignFromTeamRequest(license.product.code, license.team.id),
                licenseId = null,
                sendEmail = false
            )
        )
    }

    suspend fun getAssignLicenseRequestFromTeam(user: AppPropsHelper.AppProps.User): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    user.email,
                    user.firstName,
                    user.lastName
                ),
                false,
                AssignFromTeamRequest(license.product.code, license.team.id),
                licenseId = null,
                sendEmail = false
            )
        )
    }


    suspend fun getLicenseResponseByLicenseId(licenseId: String): LicenseResponse {
        val response = client.get(AppPropsHelper.props.customerLicensesByIdPath.replace("{licenseId}", licenseId))
        val result: LicenseResponse = response.body()
        return result
    }

    suspend fun getTeamsByLicenseCount(): List<LicenseResponse.TeamResponse> {
        val teams = getLicensesList()
            .groupBy { it.team }
            .entries
            .sortedByDescending { it.value.size }
            .map { it.key }
        require(teams.size >= 3) { "There should be at least 3 teams for this test" }
        return teams
    }

    suspend fun getSameTeamsByLicenseCount(): List<LicenseResponse.TeamResponse> {
        val teams = getLicensesList()
            .groupBy { it.team }
            .entries
            .sortedByDescending { it.value.size }
            .map { it.key }
        require(teams.isNotEmpty()) { "There should be at least 1 teams for this test" }

        return listOf(teams[0], teams[0])
    }

    suspend fun getLicenseIdByTeamId(teamId: Int): String {
        val result = getLicensesByTeamId(teamId)
        return result.first().licenseId
    }

    suspend fun getChangeTeamRequestFromTeamId(
        fromTeamId: Int,
        toTeamId: Int,
        licenseCount: Int = 1
    ): Pair<List<String>, ChangeTeamRequest> {
        val result = getLicensesByTeamId(fromTeamId)

        val licenses = result.take(licenseCount).map { it.licenseId }
        if (licenses.isEmpty()) throw IllegalStateException("No available licenses found to transfer")
        if (licenses.size < licenseCount) throw IllegalStateException("There should be at least 3 licenses in one of the team")

        return Pair(
            licenses,
            ChangeTeamRequest(licenses, toTeamId)
        )
    }

    fun rollbackChangeTeam(allRollbacks: List<Pair<List<String>, Int>>) {
        runBlocking {
            for ((licenseIds, originalTeam) in allRollbacks) {
                val rollbackRequest = ChangeTeamRequest(licenseIds, originalTeam)
                client.post(AppPropsHelper.props.customerChangeLicensesTeam) {
                    contentType(ContentType.Application.Json)
                    setBody(rollbackRequest)
                }
            }
        }
    }
}