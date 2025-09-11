package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.AssignLicenseRequest
import com.github.mikeandv.testassignment.utils.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class TestAssignLicenseNullValidation : StringSpec({
    //TODO not assiggned but isAvailableToAssign = false
    //TODO assign to another person isAvailableToAssign = false

    val client = HttpClientHelper.getAuthorizedClient()

    val testData: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestWithLicenseId().second }
    val testDataFromTeam: AssignLicenseRequest = runBlocking { TestDataHelper.getAssignLicenseRequestFromTeam().second }

    val nullVerificationTestCase: List<Pair<String, String>> = listOf(
        "email is null" to changePayloadParameterValue(testData, listOf("contact", "email"), null),
        "firstName is null" to changePayloadParameterValue(testData, listOf("contact", "firstName"), null),
        "lastName is null" to changePayloadParameterValue(testData, listOf("contact", "lastName"), null),
        "contact is null" to changePayloadParameterValue(testData, listOf("contact"), null),
        "productCode is null" to changePayloadParameterValue(testDataFromTeam, listOf("license", "productCode"), null),
        "email is not presented" to removePayloadParameterValue(testData, listOf("contact", "email")),
        "firstName is not presented" to removePayloadParameterValue(testData, listOf("contact", "firstName")),
        "lastName is not presented" to removePayloadParameterValue(testData, listOf("contact", "lastName")),
        "contact is not presented" to removePayloadParameterValue(testData, listOf("contact")),
        "productCode is not presented" to removePayloadParameterValue(testDataFromTeam, listOf("license","productCode")),
    )

    withData(
        nameFn = { (name, _) -> "invalid payload: $name" },
        ts = nullVerificationTestCase,
    ) { (name, payload) ->
        val response = runBlocking {
            client.post(AppPropsHelper.props.customerLicensesAssignPath) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }
//        val x: String = response.body()
        response.status shouldBe HttpStatusCode.BadRequest
    }
})