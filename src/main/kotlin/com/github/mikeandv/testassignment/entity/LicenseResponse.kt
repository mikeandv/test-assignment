package com.github.mikeandv.testassignment.entity

import kotlinx.serialization.Serializable

@Serializable
data class LicenseResponse(
    val licenseId: String,
    val product: ProductResponse,
    val assignee: AssigneeResponse? = null,
    val team: TeamResponse,
    val isTransferableBetweenTeams: Boolean,
    val isTrial: Boolean,
    val isSuspended: Boolean,
    val isAvailableToAssign: Boolean
) {

    @Serializable
    data class ProductResponse(val code: String, val name: String)

    @Serializable
    data class AssigneeResponse(val name: String, val email: String, val type: String)

    @Serializable
    data class TeamResponse(val id: Int, val name: String)


}
