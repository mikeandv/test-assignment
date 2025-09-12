package com.github.mikeandv.testassignment.negative

import com.github.mikeandv.testassignment.entity.Error
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.HttpClientHelper
import com.github.mikeandv.testassignment.utils.TestDataHelper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class PostAssignLicenseNegativeTests : FunSpec({
    val client = HttpClientHelper.getAuthorizedClient()


    test("License not available to assign / reason NON_PER_USER") {
        val expectedError = Error("LICENSE_IS_NOT_AVAILABLE_TO_ASSIGN", "NON_PER_USER")
        val testData = TestDataHelper.getUnavailableToAssignLicense()
        var response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }

        val actualError = Json.decodeFromString<Error>(response.bodyAsText())
        response.status shouldBe HttpStatusCode.BadRequest
        val license = TestDataHelper.getLicenseResponseByLicenseId(testData.first)
        license.assignee shouldBe null
        actualError shouldBe expectedError
    }

    test("License not available to assign / reason ALLOCATED") {
        val expectedError = Error("LICENSE_IS_NOT_AVAILABLE_TO_ASSIGN", "ALLOCATED")
        val testData = TestDataHelper.getAllocatedAssignLicense()
        var response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }


        val actualError = Json.decodeFromString<Error>(response.bodyAsText())
        response.status shouldBe HttpStatusCode.BadRequest
        val license = TestDataHelper.getLicenseResponseByLicenseId(testData.first)
        shouldNotBeNull { license.assignee }
        license.assignee?.email shouldNotBe AppPropsHelper.props.mainUserEmail
        license.assignee?.name shouldNotBe AppPropsHelper.props.mainUserFullName
        actualError shouldBe expectedError
    }

    test("License not available to assign / reason No available license found to assign in the team...") {
        val expectedError = Error("NO_AVAILABLE_LICENSE_TO_ASSIGN", "No available license found to assign in the team")
        val testData = TestDataHelper.getUnavailableToAssignLicenseeFromTeam()
        var response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }

        val actualError = Json.decodeFromString<Error>(response.bodyAsText())
        response.status shouldBe HttpStatusCode.BadRequest
        val license = TestDataHelper.getLicenseResponseByLicenseId(testData.first)
        license.assignee shouldBe null
        actualError.code shouldBe expectedError.code
        actualError.description shouldStartWith expectedError.description
    }
})