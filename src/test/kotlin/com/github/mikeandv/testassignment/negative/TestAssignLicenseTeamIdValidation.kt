package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.HttpClientHelper
import com.github.mikeandv.testassignment.utils.TestDataHelper
import com.github.mikeandv.testassignment.utils.changePayloadParameterValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class TestAssignLicenseTeamIdValidation : StringSpec({
    val client = HttpClientHelper.getAuthorizedClient()
    val testDataFromTeam: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestFromTeam().second }

    val teamIdVerificationTestCases: List<Pair<String, String>> = listOf(
        "team id is Long.MAX_VALUE" to changePayloadParameterValue(
            testDataFromTeam,
            listOf("license", "team"),
            Long.MAX_VALUE
        ),
        "team id is string" to changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), "test"),
        "team id is boolean" to changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), true),
    )

    withData(
        nameFn = { (name, _) -> "invalid teamId: $name" },
        ts = teamIdVerificationTestCases,
    ) { (name, payload) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
//        val text = response.bodyAsText()
//        val error = Json.decodeFromString<Error>(text)
        response.status shouldBe HttpStatusCode.BadRequest
    }
})