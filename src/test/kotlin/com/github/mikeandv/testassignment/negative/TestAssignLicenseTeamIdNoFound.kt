package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.entity.Error
import com.github.mikeandv.testassignment.utils.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class TestAssignLicenseTeamIdNoFound : StringSpec({
    val client = HttpClientHelper.getAuthorizedClient()
    val testDataFromTeam: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestFromTeam().second }

    val teamIdNotFoundVerificationTestCases: List<Triple<String, String, Error>> = listOf(
        Triple(
            "team id is null",
            changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), null),
            Error("TEAM_NOT_FOUND", "0")
        ),
        Triple(
            "team id is not presented",
            removePayloadParameterValue(testDataFromTeam, listOf("license", "team")),
            Error("TEAM_NOT_FOUND", "0")
        ),
        Triple(
            "team id not found",
            changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Int.MAX_VALUE),
            Error("TEAM_NOT_FOUND", Int.MAX_VALUE.toString())
        ),
    )

    withData(
        nameFn = { (name, _) -> "teamId not found: $name" },
        ts = teamIdNotFoundVerificationTestCases,
    ) { (name, payload, expectedError) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        val text = response.bodyAsText()
        val error = Json.decodeFromString<Error>(text)
        response.status shouldBe HttpStatusCode.NotFound
        error shouldBe expectedError
    }
})