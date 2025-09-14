package com.github.mikeandv.testassignment.entity

import kotlinx.serialization.Serializable

@Serializable
data class LicenseResponseError(val code: String, val description: String)
