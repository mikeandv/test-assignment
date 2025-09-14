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

    suspend fun getAllocatedAssignLicense(): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        val response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(
                AssignLicenseRequest(
                    contact = AssigneeContactRequest(
                        AppPropsHelper.props.secondUser.email,
                        AppPropsHelper.props.secondUser.firstName,
                        AppPropsHelper.props.secondUser.lastName
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
                    AppPropsHelper.props.mainUser.email,
                    AppPropsHelper.props.mainUser.firstName,
                    AppPropsHelper.props.mainUser.lastName
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getAllocatedToSameUserAssignLicense(): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        val response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(
                AssignLicenseRequest(
                    contact = AssigneeContactRequest(
                        AppPropsHelper.props.mainUser.email,
                        AppPropsHelper.props.mainUser.firstName,
                        AppPropsHelper.props.mainUser.lastName
                    ),
                    includeOfflineActivationCode = false,
                    license = null,
                    licenseId = license.licenseId,
                    sendEmail = false
                )
            )
        }
        if (response.status != HttpStatusCode.OK) {
            throw IllegalStateException("Assign license to main user request failed with status ${response.status}")
        }
        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    AppPropsHelper.props.mainUser.email,
                    AppPropsHelper.props.mainUser.firstName,
                    AppPropsHelper.props.mainUser.lastName
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getAssignLicenseRequestWithLicenseId(
        email: String,
        firstName: String,
        lastName: String
    ): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    email,
                    firstName,
                    lastName,
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getUnavailableToAssignLicense(): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && !it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    AppPropsHelper.props.mainUser.email,
                    AppPropsHelper.props.mainUser.firstName,
                    AppPropsHelper.props.mainUser.lastName
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getUnavailableToAssignLicenseeFromTeam(): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList()
            .groupBy { it.team }
            .entries.firstOrNull { it.value.all { it.assignee == null && !it.isAvailableToAssign } }
            ?.value
            ?.firstOrNull() ?: throw IllegalStateException("No team with only unavailable to assign licenses")
        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    AppPropsHelper.props.mainUser.email,
                    AppPropsHelper.props.mainUser.firstName,
                    AppPropsHelper.props.mainUser.lastName
                ),
                false,
                AssignFromTeamRequest(license.product.code, license.team.id),
                licenseId = null,
                sendEmail = false
            )
        )
    }

    suspend fun getAssignLicenseRequestFromTeam(
        email: String,
        firstName: String,
        lastName: String
    ): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    email,
                    firstName,
                    lastName
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

    suspend fun getLicenseCountByTeamId(teamId: Int): Int {
        val response =
            client.get(AppPropsHelper.props.customerTeamLicensesByTeamIdPath.replace("{teamId}", teamId.toString()))
        val result: List<LicenseResponse> = response.body()
        return result.size
    }

    suspend fun getLicenseIdByTeamId(teamId: Int): String {
        val response =
            client.get(AppPropsHelper.props.customerTeamLicensesByTeamIdPath.replace("{teamId}", teamId.toString()))
        val result: List<LicenseResponse> = response.body()
        return result.first().licenseId
    }

    suspend fun getChangeTeamRequestFromTeamId(
        fromTeamId: Int,
        toTeamId: Int,
        licenseCount: Int = 1
    ): Pair<List<String>, ChangeTeamRequest> {
        val response =
            client.get(AppPropsHelper.props.customerTeamLicensesByTeamIdPath.replace("{teamId}", fromTeamId.toString()))
        val result: List<LicenseResponse> = response.body()

        val licenses = result.take(licenseCount).map { it.licenseId }
        if (licenses.isEmpty()) throw IllegalStateException("No available licenses found to transfer")
        if (licenses.size < licenseCount) throw IllegalStateException("There should be at least 3 licenses in one of the team")

        return Pair(
            licenses,
            ChangeTeamRequest(licenses, toTeamId)
        )
    }


    suspend fun getAssignedLicenceCount(): Int {
        return getLicensesList().filter {
            it.assignee != null
                    && it.assignee.email == AppPropsHelper.props.mainUser.email
                    && it.assignee.name == AppPropsHelper.props.mainUser.fullName
        }.size
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