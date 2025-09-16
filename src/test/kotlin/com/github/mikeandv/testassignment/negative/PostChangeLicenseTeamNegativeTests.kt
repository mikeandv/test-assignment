package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runResponseOnlyTests
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.StatusAndBodyCase
import com.github.mikeandv.testassignment.utils.TestDataHelper.getChangeTeamRequestFromTeamId
import com.github.mikeandv.testassignment.utils.TestDataHelper.getTeamsByLicenseCount
import com.github.mikeandv.testassignment.utils.changePayloadParameterValue
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import io.ktor.http.*

class PostChangeLicenseTeamNegativeTests : FunSpec({
    val data = runBlocking {
        val (fromTeam, toTEam) = getTeamsByLicenseCount()
        getChangeTeamRequestFromTeamId(fromTeam.id, toTEam.id).second
    }

    runResponseOnlyTests(
        "licenseId not found",
        listOf(
            StatusAndBodyCase(
                "not_existing_license_id",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("licenseIds"), listOf("not_existing_license_id")),
                HttpStatusCode.OK,
                "{\"licenseIds\":[]}"
            ),
            StatusAndBodyCase(
                "empty",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("licenseIds"), listOf("")),
                HttpStatusCode.OK,
                "{\"licenseIds\":[]}"
            ),
            StatusAndBodyCase(
                "123",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("licenseIds"), listOf(123)),
                HttpStatusCode.OK,
                "{\"licenseIds\":[]}"
            ),
            StatusAndBodyCase(
                "true",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("licenseIds"), listOf(true)),
                HttpStatusCode.OK,
                "{\"licenseIds\":[]}"
            ),
            StatusAndBodyCase(
                "empty list",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("licenseIds"), listOf<String>()),
                HttpStatusCode.OK,
                "{\"licenseIds\":[]}"
            ),
        )
    )
})