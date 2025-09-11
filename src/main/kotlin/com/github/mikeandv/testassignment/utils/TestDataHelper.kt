package com.github.mikeandv.testassignment.utils

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.AssignLicenseRequest.AssignFromTeamRequest
import com.github.mikeandv.testassignment.entity.AssignLicenseRequest.AssigneeContactRequest
import com.github.mikeandv.testassignment.entity.ChangeTeamRequest
import com.github.mikeandv.testassignment.entity.LicenseResponse
import io.ktor.client.call.*
import io.ktor.client.request.*

object TestDataHelper {
    val client = HttpClientHelper.getAuthorizedClient()


    private suspend fun getLicensesList(): List<LicenseResponse> {
        val response = client.get(AppPropsHelper.props.customerLicensesPath)
        return response.body()
    }


    suspend fun getFakeUserAssignLicenseRequestWithLicenseId(): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    AppPropsHelper.props.fakeEmail,
                    AppPropsHelper.props.fakeFirstName,
                    AppPropsHelper.props.fakeLastName
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getAssignLicenseRequestWithLicenseId(): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    AppPropsHelper.props.email,
                    AppPropsHelper.props.firstName,
                    AppPropsHelper.props.lastName
                ),
                includeOfflineActivationCode = false,
                license = null,
                licenseId = license.licenseId,
                sendEmail = false
            )
        )
    }

    suspend fun getAssignLicenseRequestFromTeam(): Pair<String, AssignLicenseRequest> {
        val license = getLicensesList().firstOrNull { it.assignee == null && it.isAvailableToAssign }
            ?: throw IllegalStateException("No available license found without assignee")

        return Pair(
            license.licenseId,
            AssignLicenseRequest(
                contact = AssigneeContactRequest(
                    AppPropsHelper.props.email,
                    AppPropsHelper.props.firstName,
                    AppPropsHelper.props.lastName
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
        return getLicensesList()
            .groupBy { it.team }
            .entries
            .sortedByDescending { it.value.size }
            .map { it.key }
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

        return Pair(
            licenses,
            ChangeTeamRequest(licenses, toTeamId)
        )
    }

    suspend fun getAssignedLicenceCount(): Int {
        return getLicensesList().filter {
            it.assignee != null
                    && it.assignee.email == AppPropsHelper.props.email
                    && it.assignee.name == AppPropsHelper.props.fullName
        }.size
    }


}