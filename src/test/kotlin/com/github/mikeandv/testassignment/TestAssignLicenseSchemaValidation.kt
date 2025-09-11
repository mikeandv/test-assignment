package com.github.mikeandv.testassignment

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.Error
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.HttpClientHelper
import com.github.mikeandv.testassignment.utils.TestDataHelper
import com.github.mikeandv.testassignment.utils.changePayloadParameterValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class TestAssignLicenseSchemaValidation : StringSpec({
    val client = HttpClientHelper.getAuthorizedClient()
    val testData: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestWithLicenseId().second }

    val testCases: List<Triple<String, String, Error>> = listOf(
        Triple(
            "firstName is not string",
            changePayloadParameterValue(testData, listOf("contact", "firstName"), 123),
            Error("INVALID_CONTACT_NAME", "Sorry, we can't accept digits in this field.")
        ),
        Triple(
            "lastName is not string",
            changePayloadParameterValue(testData, listOf("contact", "lastName"), 123),
            Error("INVALID_CONTACT_NAME", "Sorry, we can't accept digits in this field.")
        ),
        Triple(
            "licenseId is null and license is null",
            changePayloadParameterValue(testData, listOf("licenseId"), null),
            Error("MISSING_FIELD", "Either licenseId or license must be provided")
        ),
    )

    withData(
        nameFn = { (name, _) -> "invalid payload: $name" },
        ts = testCases,
    ) { (name, payload, expectedError) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        val text = response.bodyAsText()
        val error = Json.decodeFromString<Error>(text)
        response.status shouldBe HttpStatusCode.BadRequest
        error shouldBe expectedError

    }
})