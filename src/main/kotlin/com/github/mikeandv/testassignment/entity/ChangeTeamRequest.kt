package com.github.mikeandv.testassignment.entity

import kotlinx.serialization.Serializable

@Serializable
data class ChangeTeamRequest(val licenseIds: List<String>, val targetTeamId: Int)
