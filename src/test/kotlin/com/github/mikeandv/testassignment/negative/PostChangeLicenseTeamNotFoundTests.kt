package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.LicenseResponseError
import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runErrorTests
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.ErrorAndStatusCase
import com.github.mikeandv.testassignment.utils.TestDataHelper.getChangeTeamRequestFromTeamId
import com.github.mikeandv.testassignment.utils.TestDataHelper.getTeamsByLicenseCount
import com.github.mikeandv.testassignment.utils.changePayloadParameterValue
import com.github.mikeandv.testassignment.utils.removePayloadParameterValue
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import io.ktor.http.*

class PostChangeLicenseTeamNotFoundTests : FunSpec({
    val data = runBlocking {
        val (fromTeam, toTEam) = getTeamsByLicenseCount()
        getChangeTeamRequestFromTeamId(fromTeam.id, toTEam.id).second
    }

    runErrorTests(
        "targetTeamId not found",
        listOf(
            ErrorAndStatusCase(
                "null",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), null),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            ),
            ErrorAndStatusCase(
                "team id is not presented",
                AppPropsHelper.props.customerChangeLicensesTeam,
                removePayloadParameterValue(data, listOf("targetTeamId")),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            ),
            ErrorAndStatusCase(
                "${Int.MAX_VALUE}",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), Int.MAX_VALUE),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", Int.MAX_VALUE.toString())
            ),
            ErrorAndStatusCase(
                "${Int.MIN_VALUE}",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), Int.MIN_VALUE),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", Int.MIN_VALUE.toString())
            ),
            ErrorAndStatusCase(
                "${Double.MIN_VALUE}",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), Double.MIN_VALUE),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            ),
            ErrorAndStatusCase(
                "0,0",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), 0.0),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            )
        )
    )
})


