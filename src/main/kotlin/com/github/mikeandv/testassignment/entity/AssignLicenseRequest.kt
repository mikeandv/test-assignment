package com.github.mikeandv.testassignment.entity

import kotlinx.serialization.Serializable

@Serializable
data class AssignLicenseRequest(
    val contact: AssigneeContactRequest,
    val includeOfflineActivationCode: Boolean,
    val license: AssignFromTeamRequest?,
    val licenseId: String?,
    val sendEmail: Boolean
) {
    @Serializable
    data class AssigneeContactRequest(val email: String, val firstName: String, val lastName: String)

    @Serializable
    data class AssignFromTeamRequest(val productCode: String, val team: Int?)
}
