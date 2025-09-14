package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.LicenseResponseError
import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runErrorTests
import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runStatusOnlyTests
import com.github.mikeandv.testassignment.utils.*
import io.kotest.core.spec.style.FunSpec
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class PostAssignLicenseValidationTests : FunSpec({
    val testDataFromTeam: AssignLicenseRequest = runBlocking {
        TestDataHelper.getAssignLicenseRequestFromTeam(
            AppPropsHelper.props.mainUser.email,
            AppPropsHelper.props.mainUser.firstName,
            AppPropsHelper.props.mainUser.lastName
        ).second
    }
    val testData: AssignLicenseRequest = runBlocking {
        TestDataHelper.getAssignLicenseRequestWithLicenseId(
            AppPropsHelper.props.mainUser.email,
            AppPropsHelper.props.mainUser.firstName,
            AppPropsHelper.props.mainUser.lastName
        ).second
    }

    runErrorTests(
        "firstName not valid",
        listOf(
            ErrorAndStatusCase(
                "123",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "firstName"), 123),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_NAME", "Sorry, we can't accept digits in this field.")
            ),
            ErrorAndStatusCase(
                "is empty",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "firstName"), ""),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_NAME", "This field can't be empty.")
            )
        )
    )

    runErrorTests(
        "lastName not valid",
        listOf(
            ErrorAndStatusCase(
                "123",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "lastName"), 123),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_NAME", "Sorry, we can't accept digits in this field.")
            ),
            ErrorAndStatusCase(
                "empty",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "lastName"), ""),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_NAME", "This field can't be empty.")
            ),
        )
    )

    runErrorTests(
        "both licenceId and license",
        listOf(
            ErrorAndStatusCase(
                "null",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("licenseId"), null),
                HttpStatusCode.BadRequest,
                LicenseResponseError("MISSING_FIELD", "Either licenseId or license must be provided")
            ),
        )
    )

    runErrorTests(
        "email not valid",
        listOf(
            ErrorAndStatusCase(
                "123",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "email"), 123),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_EMAIL", "123")
            ),
            ErrorAndStatusCase(
                "wrong_email",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "email"), "wrong_email"),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_EMAIL", "wrong_email")
            ),
            ErrorAndStatusCase(
                "wrong_email#@test",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "email"), "wrong_email#@test"),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_EMAIL", "wrong_email#@test")
            ),
            ErrorAndStatusCase(
                "empty string",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "email"), ""),
                HttpStatusCode.BadRequest,
                LicenseResponseError("INVALID_CONTACT_EMAIL", "")
            )
        )
    )

    runStatusOnlyTests(
        "missing field",
        listOf(
            StatusOnlyCase(
                "email",
                AppPropsHelper.props.customerLicensesAssignPath,
                removePayloadParameterValue(testData, listOf("contact", "email")),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "firstName",
                AppPropsHelper.props.customerLicensesAssignPath,
                removePayloadParameterValue(testData, listOf("contact", "firstName")),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "lastName",
                AppPropsHelper.props.customerLicensesAssignPath,
                removePayloadParameterValue(testData, listOf("contact", "lastName")),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "contact",
                AppPropsHelper.props.customerLicensesAssignPath,
                removePayloadParameterValue(testData, listOf("contact")),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "productCode",
                AppPropsHelper.props.customerLicensesAssignPath,
                removePayloadParameterValue(testDataFromTeam, listOf("license", "productCode")),
                HttpStatusCode.BadRequest
            ),
        )
    )

    runStatusOnlyTests(
        "null field",
        listOf(
            StatusOnlyCase(
                "email",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "email"), null),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "firstName",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "firstName"), null),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "lastName",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact", "lastName"), null),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "contact",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testData, listOf("contact"), null),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "productCode ",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), null),
                HttpStatusCode.BadRequest
            ),
        )
    )

    runStatusOnlyTests(
        "empty object field",
        listOf(
            StatusOnlyCase(
                "contact",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("contact"), Unit),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "license",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license"), Unit),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "json root",
                AppPropsHelper.props.customerLicensesAssignPath,
                getEmptyJsonObject(),
                HttpStatusCode.BadRequest
            )
        )
    )

    runStatusOnlyTests(
        "teamId not valid",
        listOf(
            StatusOnlyCase(
                "${Long.MAX_VALUE}",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Long.MAX_VALUE),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "${Long.MIN_VALUE}",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Long.MIN_VALUE),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "${Double.MAX_VALUE}",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(
                    testDataFromTeam,
                    listOf("license", "team"),
                    Double.MAX_VALUE
                ),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "string_id",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), "string_id"),
                HttpStatusCode.BadRequest
            ),
            StatusOnlyCase(
                "true",
                AppPropsHelper.props.customerLicensesAssignPath,
                changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), true),
                HttpStatusCode.BadRequest
            ),
        )
    )

    runStatusOnlyTests(
        "payload is empty",
        listOf(
            StatusOnlyCase(
                "empty",
                AppPropsHelper.props.customerLicensesAssignPath,
                "",
                HttpStatusCode.BadRequest
            )
        )
    )
})
