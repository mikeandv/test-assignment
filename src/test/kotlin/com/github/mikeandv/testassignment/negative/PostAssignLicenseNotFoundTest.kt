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

class PostAssignLicenseNotFoundTest : FunSpec({
    val client = HttpClientHelper.getAuthorizedClient()
    val testDataFromTeam: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestFromTeam().second }
    val testData: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestWithLicenseId().second }


    val teamIdNotFoundTestCases: List<Triple<Pair<String, String>, String, Error>> = listOf(
        Triple(
            Pair("teamId", "null"),
            changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), null),
            Error("TEAM_NOT_FOUND", "0")
        ),
        Triple(
            Pair("teamId", "team id is not presented"),
            removePayloadParameterValue(testDataFromTeam, listOf("license", "team")),
            Error("TEAM_NOT_FOUND", "0")
        ),
        Triple(
            Pair("teamId", "${Int.MAX_VALUE}"),
            changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Int.MAX_VALUE),
            Error("TEAM_NOT_FOUND", Int.MAX_VALUE.toString())
        ),
        Triple(
            Pair("teamId", "${Int.MIN_VALUE}"),
            changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Int.MIN_VALUE),
            Error("TEAM_NOT_FOUND", Int.MIN_VALUE.toString())
        ),
        Triple(
            Pair("teamId", "${Double.MIN_VALUE}"),
            changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), Double.MIN_VALUE),
            Error("TEAM_NOT_FOUND", "0")
        ),
        Triple(
            Pair("teamId", "0,0"),
            changePayloadParameterValue(testDataFromTeam, listOf("license", "team"), 0.0),
            Error("TEAM_NOT_FOUND", "0")
        ),
        Triple(
            Pair("licenseId", "not_existing_license_id"),
            changePayloadParameterValue(testData, listOf("licenseId"), "not_existing_license_id"),
            Error("LICENSE_NOT_FOUND", "not_existing_license_id")
        ),
        Triple(
            Pair("licenseId", "empty string"),
            changePayloadParameterValue(testData, listOf("licenseId"), ""),
            Error("LICENSE_NOT_FOUND", "")
        ),
        Triple(
            Pair("productCode", "not_existing_license_id"),
            changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), "not_existing_license_id"),
            Error("PRODUCT_NOT_FOUND", "not_existing_license_id")
        ),
        Triple(
            Pair("productCode", "empty string"),
            changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), ""),
            Error("PRODUCT_NOT_FOUND", "")
        ),
    )

    withData(
        nameFn = { (name, _) -> "Not found / ${name.first} not found / should return 404 when value: ${name.second}" },
        ts = teamIdNotFoundTestCases,
    ) { (name, payload, expectedError) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
        val error = Json.decodeFromString<Error>(response.bodyAsText())
        response.status shouldBe HttpStatusCode.NotFound
        error shouldBe expectedError
    }
})