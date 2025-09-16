package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.LicenseResponseError
import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runErrorTests
import com.github.mikeandv.testassignment.utils.*
import io.kotest.core.spec.style.FunSpec
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class PostAssignLicenseNotFoundTest : FunSpec({
    val testDataFromTeam: AssignLicenseRequest = runBlocking {
        TestDataHelper.getAssignLicenseRequestFromTeam(AppPropsHelper.props.mainUser).second
    }
    val testData: AssignLicenseRequest = runBlocking {
        TestDataHelper.getAssignLicenseRequestWithLicenseId(
            AppPropsHelper.props.mainUser
        ).second
    }

    runErrorTests(
        "teamId not found",
        listOf(
            ErrorAndStatusCase(
                "null",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), null),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            ),
            ErrorAndStatusCase(
                "team id is not presented",
                AppPropsHelper.props.customerLicensesAssignPath,
                removePayloadParameterValue(testDataFromTeam, listOf("license", "team")),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            ),
            ErrorAndStatusCase(
                "${Int.MAX_VALUE}",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Int.MAX_VALUE),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", Int.MAX_VALUE.toString())
            ),
            ErrorAndStatusCase(
                "${Int.MIN_VALUE}",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Int.MIN_VALUE),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", Int.MIN_VALUE.toString())
            ),
            ErrorAndStatusCase(
                "${Double.MIN_VALUE}",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Double.MIN_VALUE),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            ),
            ErrorAndStatusCase(
                "0,0",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), 0.0),
                HttpStatusCode.NotFound,
                LicenseResponseError("TEAM_NOT_FOUND", "0")
            )
        )
    )

    runErrorTests(
        "licenseId not found",
        listOf(
            ErrorAndStatusCase(
                "not_existing_license_id",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("licenseId"), "not_existing_license_id"),
                HttpStatusCode.NotFound,
                LicenseResponseError("LICENSE_NOT_FOUND", "not_existing_license_id")
            ),
            ErrorAndStatusCase(
                "empty string",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("licenseId"), ""),
                HttpStatusCode.NotFound,
                LicenseResponseError("LICENSE_NOT_FOUND", "")
            ),
            ErrorAndStatusCase(
                "123",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("licenseId"), 123),
                HttpStatusCode.NotFound,
                LicenseResponseError("LICENSE_NOT_FOUND", "123")
            ),
            ErrorAndStatusCase(
                "true",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("licenseId"), true),
                HttpStatusCode.NotFound,
                LicenseResponseError("LICENSE_NOT_FOUND", "true")
            ),
        )
    )

    runErrorTests(
        "productCode not found",
        listOf(
            ErrorAndStatusCase(
                "not_exists",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), "not_exists"),
                HttpStatusCode.NotFound,
                LicenseResponseError("PRODUCT_NOT_FOUND", "not_exists")
            ),
            ErrorAndStatusCase(
                "empty string",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), ""),
                HttpStatusCode.NotFound,
                LicenseResponseError("PRODUCT_NOT_FOUND", "")
            ),
            ErrorAndStatusCase(
                "123",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), 123),
                HttpStatusCode.NotFound,
                LicenseResponseError("PRODUCT_NOT_FOUND", "123")
            ),
            ErrorAndStatusCase(
                "true",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), true),
                HttpStatusCode.NotFound,
                LicenseResponseError("PRODUCT_NOT_FOUND", "true")
            ),
        )
    )
})