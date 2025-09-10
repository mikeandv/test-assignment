package com.github.mikeandv.testassignment.utils

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.AssignLicenseRequest.AssignFromTeamRequest
import com.github.mikeandv.testassignment.entity.AssignLicenseRequest.AssigneeContactRequest
import com.github.mikeandv.testassignment.entity.LicenseResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.collections.firstOrNull

object TestDataHelper {
    val client = HttpClientHelper.getAuthorizedClient()

    suspend fun getAssignLicenseRequestWithLicenseId(): Pair<String, AssignLicenseRequest> {
        val response = client.get(AppPropsHelper.props.customerLicensesPath)
        val result: List<LicenseResponse> = response.body()
        val license = result.firstOrNull { it.assignee == null && it.isAvailableToAssign }
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
        val response = client.get(AppPropsHelper.props.customerLicensesPath)
        val result: List<LicenseResponse> = response.body()
        val license = result.firstOrNull { it.assignee == null && it.isAvailableToAssign }
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

    suspend fun getTeamIds(): List<Int> {
        val response = client.get(AppPropsHelper.props.customerLicensesPath)
        val result: List<LicenseResponse> = response.body()
        return result.map { it.team.id }.distinct()
    }

    suspend fun getLicenseCountByTeamId(teamId: Int): Int {
        val response =
            client.get(AppPropsHelper.props.customerTeamLicensesByTeamIdPath.replace("{teamId}", teamId.toString()))
        val f: String = response.body()
        val result: List<LicenseResponse> = response.body()

        return result.size
    }

    suspend fun getAssignedLicenceCount(): Int {
        val response = client.get(AppPropsHelper.props.customerLicensesPath)
        val result: List<LicenseResponse> = response.body()
        return result.filter {
            it.assignee != null
                    && it.assignee.email == AppPropsHelper.props.email
                    && it.assignee.name == AppPropsHelper.props.fullName
        }.size
    }
}