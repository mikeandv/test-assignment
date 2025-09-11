package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.Error
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.HttpClientHelper
import com.github.mikeandv.testassignment.utils.TestDataHelper
import com.github.mikeandv.testassignment.utils.changePayloadParameterValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class TestAssignLicenseEmailValidation : StringSpec({
    val client = HttpClientHelper.getAuthorizedClient()
    val testData: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestWithLicenseId().second }

    val emailVerificationTestCases: List<Triple<String, String, Error>> = listOf(
        Triple(
            "email is number",
            changePayloadParameterValue(testData, listOf("contact", "email"), 123),
            Error("INVALID_CONTACT_EMAIL", "123")
        ),
        Triple(
            "email is text",
            changePayloadParameterValue(testData, listOf("contact", "email"), "test"),
            Error("INVALID_CONTACT_EMAIL", "test")
        ),
        Triple(
            "email is not valid",
            changePayloadParameterValue(testData, listOf("contact", "email"), "test#@test"),
            Error("INVALID_CONTACT_EMAIL", "test#@test")
        ),
    )

    withData(
        nameFn = { (name, _) -> "invalid email: $name" },
        ts = emailVerificationTestCases,
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