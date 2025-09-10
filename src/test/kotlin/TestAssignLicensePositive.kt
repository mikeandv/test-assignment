package com.github.mikeandv.testassignment

import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.HttpClientHelper
import com.github.mikeandv.testassignment.utils.TestDataHelper.getAssignLicenseRequestWithLicenseId
import com.github.mikeandv.testassignment.utils.TestDataHelper.getAssignLicenseRequestFromTeam
import com.github.mikeandv.testassignment.utils.TestDataHelper.getAssignedLicenceCount
import com.github.mikeandv.testassignment.utils.TestDataHelper.getLicenseResponseByLicenseId
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*

class TestAssignLicensePositive : StringSpec({
    val client = HttpClientHelper.getAuthorizedClient()

    "POST customer/licenses/assign assign single license by id" {
        val expectedCount = getAssignedLicenceCount() + 1
        val testData = getAssignLicenseRequestWithLicenseId()
        var response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }

        response.status shouldBe HttpStatusCode.OK
        getAssignedLicenceCount() shouldBe expectedCount
        val license = getLicenseResponseByLicenseId(testData.first)
        license.assignee?.email shouldBe AppPropsHelper.props.email
        license.assignee?.name shouldBe AppPropsHelper.props.fullName
    }

    "POST customer/licenses/assign assign single license from team" {
        val expectedCount = getAssignedLicenceCount() + 1
        val testData = getAssignLicenseRequestFromTeam()
        var response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }

        response.status shouldBe HttpStatusCode.OK
        getAssignedLicenceCount() shouldBe expectedCount
        val license = getLicenseResponseByLicenseId(testData.first)
        license.assignee?.email shouldBe AppPropsHelper.props.email
        license.assignee?.name shouldBe AppPropsHelper.props.fullName
    }

//    afterEach {
//        var response = client.post("customer/licenses/revoke") {
//            parameter("licenseId", "G9JQ55G21J")
//        }
//        response.status shouldBe HttpStatusCode.OK
//    }


})