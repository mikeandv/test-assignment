package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.Error
import com.github.mikeandv.testassignment.utils.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class PostAssignLicenseValidationTests : FunSpec({
    val client = HttpClientHelper.getAuthorizedClient()
    val testData: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestWithLicenseId().second }
    val testDataFromTeam: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestFromTeam().second }

    val testCases: List<Triple<String, String, Error>> = listOf(
        Triple(
            "firstName 123",
            changePayloadParameterValue(testData, listOf("contact", "firstName"), 123),
            Error("INVALID_CONTACT_NAME", "Sorry, we can't accept digits in this field.")
        ),
        Triple(
            "firstName is empty",
            changePayloadParameterValue(testData, listOf("contact", "firstName"), ""),
            Error("INVALID_CONTACT_NAME", "This field can't be empty.")
        ),
        Triple(
            "lastName 123",
            changePayloadParameterValue(testData, listOf("contact", "lastName"), 123),
            Error("INVALID_CONTACT_NAME", "Sorry, we can't accept digits in this field.")
        ),
        Triple(
            "lastName is empty",
            changePayloadParameterValue(testData, listOf("contact", "lastName"), ""),
            Error("INVALID_CONTACT_NAME", "This field can't be empty.")
        ),
        Triple(
            "licenseId is null and license is null",
            changePayloadParameterValue(testData, listOf("licenseId"), null),
            Error("MISSING_FIELD", "Either licenseId or license must be provided")
        ),
    )

    withData(
        nameFn = { (name, _) -> "Field validation / should return 400 for $name" },
        ts = testCases,
    ) { (name, payload, expectedError) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        val error = Json.decodeFromString<Error>(response.bodyAsText())
        response.status shouldBe HttpStatusCode.BadRequest
        error shouldBe expectedError

    }


    val missingVerificationTestCase: List<Pair<String, String>> = listOf(
        "email" to removePayloadParameterValue(testData, listOf("contact", "email")),
        "firstName" to removePayloadParameterValue(testData, listOf("contact", "firstName")),
        "lastName" to removePayloadParameterValue(testData, listOf("contact", "lastName")),
        "contact" to removePayloadParameterValue(testData, listOf("contact")),
        "productCode" to removePayloadParameterValue(testDataFromTeam, listOf("license", "productCode")),
    )

    withData(
        nameFn = { (field, _) -> "Schema Validation / missing fields / should return 400 when $field is missing" },
        ts = missingVerificationTestCase,
    ) { (field, payload) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        response.status shouldBe HttpStatusCode.BadRequest
    }

    val nullVerificationTestCase: List<Pair<String, String>> = listOf(
        "email" to changePayloadParameterValue(testData, listOf("contact", "email"), null),
        "firstName" to changePayloadParameterValue(testData, listOf("contact", "firstName"), null),
        "lastName" to changePayloadParameterValue(testData, listOf("contact", "lastName"), null),
        "contact" to changePayloadParameterValue(testData, listOf("contact"), null),
        "productCode " to changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), null),
    )

    withData(
        nameFn = { (field, _) -> "Schema Validation / null values / should return 400 when $field is null" },
        ts = nullVerificationTestCase,
    ) { (field, payload) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        response.status shouldBe HttpStatusCode.BadRequest
    }

    val emptyJsonObjectVerificationTestCase: List<Pair<String, String>> = listOf(
        "contact" to changePayloadParameterValue(testDataFromTeam, listOf("contact"), Unit),
        "license" to changePayloadParameterValue(testDataFromTeam, listOf("license"), Unit),
        "json root" to getEmptyJsonObject()
    )

    withData(
        nameFn = { (field, _) -> "Schema Validation / empty json object / should return 400 when $field is empty object" },
        ts = emptyJsonObjectVerificationTestCase,
    ) { (field, payload) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        response.status shouldBe HttpStatusCode.BadRequest
    }

    val emailVerificationTestCases: List<Triple<String, String, Error>> = listOf(
        Triple(
            "123",
            changePayloadParameterValue(testData, listOf("contact", "email"), 123),
            Error("INVALID_CONTACT_EMAIL", "123")
        ),
        Triple(
            "wrong_email",
            changePayloadParameterValue(testData, listOf("contact", "email"), "wrong_email"),
            Error("INVALID_CONTACT_EMAIL", "wrong_email")
        ),
        Triple(
            "wrong_email#@test",
            changePayloadParameterValue(testData, listOf("contact", "email"), "wrong_email#@test"),
            Error("INVALID_CONTACT_EMAIL", "wrong_email#@test")
        ),
        Triple(
            "empty string",
            changePayloadParameterValue(testData, listOf("contact", "email"), ""),
            Error("INVALID_CONTACT_EMAIL", "")
        ),
    )

    withData(
        nameFn = { (name, _) -> "Field validation / invalid email / should return 400 when email: $name" },
        ts = emailVerificationTestCases,
    ) { (name, payload, expectedError) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        val error = Json.decodeFromString<Error>(response.bodyAsText())
        response.status shouldBe HttpStatusCode.BadRequest
        error shouldBe expectedError
    }

    val teamIdVerificationTestCases: List<Pair<String, String>> = listOf(
        "${Long.MAX_VALUE}" to changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Long.MAX_VALUE),
        "${Long.MIN_VALUE}" to changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Long.MIN_VALUE),
        "${Double.MAX_VALUE}" to changePayloadParameterValue(
            testDataFromTeam,
            listOf("license", "team"),
            Double.MAX_VALUE
        ),
        "string_id" to changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), "string_id"),
        "true" to changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), true),
    )

    withData(
        nameFn = { (name, _) -> "Field validation / invalid team id / should return 400 when teamid: $name" },
        ts = teamIdVerificationTestCases,
    ) { (name, payload) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        response.status shouldBe HttpStatusCode.BadRequest
    }

    test("Schema Validation / empty payload / should return 400 when payload is empty ") {
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody("")
            }
        }
        response.status shouldBe HttpStatusCode.BadRequest
    }
})
