package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runStatusOnlyTests
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.StatusOnlyCase
import com.github.mikeandv.testassignment.utils.TestDataHelper.getChangeTeamRequestFromTeamId
import com.github.mikeandv.testassignment.utils.TestDataHelper.getTeamsByLicenseCount
import com.github.mikeandv.testassignment.utils.changePayloadParameterValue
import com.github.mikeandv.testassignment.utils.removePayloadParameterValue
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import io.ktor.http.*

class PostChangeLicenseTeamValidationTests : FunSpec({
    val data = runBlocking {
        val (fromTeam, toTEam) = getTeamsByLicenseCount()
        getChangeTeamRequestFromTeamId(fromTeam.id, toTEam.id).second
    }

    runStatusOnlyTests(
        "teamId not valid",
        listOf(
            StatusOnlyCase(
                "${Long.MAX_VALUE}",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), Long.MAX_VALUE),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "${Long.MIN_VALUE}",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), Long.MIN_VALUE),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "${Double.MAX_VALUE}",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(
                    data,
                    listOf("targetTeamId"),
                    Double.MAX_VALUE
                ),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "string_id",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), "string_id"),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "true",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("targetTeamId"), true),
                HttpStatusCode.BadRequest
            )
        )
    )

    runStatusOnlyTests(
        "licenseIds not valid",
        listOf(
            StatusOnlyCase(
                "null",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("licenseIds"), null),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "list with null",
                AppPropsHelper.props.customerChangeLicensesTeam,
                changePayloadParameterValue(data, listOf("licenseIds"), listOf(null)),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "list with null",
                AppPropsHelper.props.customerChangeLicensesTeam,
                removePayloadParameterValue(data, listOf("licenseIds")),
                HttpStatusCode.BadRequest
            )
        )
    )

    runStatusOnlyTests(
        "payload is empty",
        listOf(
            StatusOnlyCase(
                "empty",
                AppPropsHelper.props.customerChangeLicensesTeam,
                "",
                HttpStatusCode.BadRequest
            )
        )
    )
})