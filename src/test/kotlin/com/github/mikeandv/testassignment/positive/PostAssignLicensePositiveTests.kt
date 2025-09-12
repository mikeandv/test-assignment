package com.github.mikeandv.testassignment.positive

import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.HttpClientHelper
import com.github.mikeandv.testassignment.utils.TestDataHelper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*

class PostAssignLicensePositiveTests : StringSpec({
    val client = HttpClientHelper.getAuthorizedClient()

    "POST customer/licenses/assign assign single license by id" {
        val testData = TestDataHelper.getAssignLicenseRequestWithLicenseId()
        var response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }

        response.status shouldBe HttpStatusCode.OK
        val license = TestDataHelper.getLicenseResponseByLicenseId(testData.first)
        license.assignee?.email shouldBe AppPropsHelper.props.mainUserEmail
        license.assignee?.name shouldBe AppPropsHelper.props.mainUserFullName
    }

    "POST customer/licenses/assign assign single license from team" {
        val testData = TestDataHelper.getAssignLicenseRequestFromTeam()
        var response = client.post(AppPropsHelper.props.customerLicensesAssignPath) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }

        response.status shouldBe HttpStatusCode.OK
        val license = TestDataHelper.getLicenseResponseByLicenseId(testData.first)
        license.assignee?.email shouldBe AppPropsHelper.props.mainUserEmail
        license.assignee?.name shouldBe AppPropsHelper.props.mainUserFullName
    }

    //TODO Corner case reassign to same person

})