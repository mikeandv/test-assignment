package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.LicenseResponseError
import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runErrorWithContainsTest
import com.github.mikeandv.testassignment.utils.AssignLicenseErrorExtendedCase
import com.github.mikeandv.testassignment.utils.TestDataHelper.getAllocatedAssignLicense
import com.github.mikeandv.testassignment.utils.TestDataHelper.getUnavailableToAssignLicense
import com.github.mikeandv.testassignment.utils.TestDataHelper.getUnavailableToAssignLicenseeFromTeam
import io.kotest.core.spec.style.FunSpec
import io.ktor.http.*

class PostAssignLicenseNegativeTests : FunSpec({

    runErrorWithContainsTest(
        "License not available to assign ",
        listOf(
            AssignLicenseErrorExtendedCase(
                "NON_PER_USER",
                { getUnavailableToAssignLicense() },
                HttpStatusCode.BadRequest,
                LicenseResponseError("LICENSE_IS_NOT_AVAILABLE_TO_ASSIGN", "NON_PER_USER"),
            ),
            AssignLicenseErrorExtendedCase(
                "No available license found to assign in the team",
                { getUnavailableToAssignLicenseeFromTeam() },
                HttpStatusCode.BadRequest,
                LicenseResponseError("NO_AVAILABLE_LICENSE_TO_ASSIGN", "No available license found to assign in the team")
            ),
            AssignLicenseErrorExtendedCase(
                "ALLOCATED",
                { getAllocatedAssignLicense() },
                HttpStatusCode.BadRequest,
                LicenseResponseError("LICENSE_IS_NOT_AVAILABLE_TO_ASSIGN", "ALLOCATED")
            )
        )
    )
})