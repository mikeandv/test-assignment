package com.github.mikeandv.testassignment.positive

import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.HttpClientHelper
import com.github.mikeandv.testassignment.utils.TestDataHelper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*

class TestChangeLicenseTeamPositive : StringSpec({
    val client = HttpClientHelper.getAuthorizedClient()


    "POST /customer/changeLicensesTeam change single license team" {
        val teams = TestDataHelper.getTeamsByLicenseCount()
        require(teams.size >= 2) { "There should be at least 2 teams for this test" }
        val (fromTeam, toTeam) = teams
        val expectedCount = TestDataHelper.getLicenseCountByTeamId(toTeam.id) + 1


        val testData = TestDataHelper.getChangeTeamRequestFromTeamId(fromTeam.id, toTeam.id)
        var response = client.post(AppPropsHelper.props.customerChangeLicensesTeam) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }
        response.status shouldBe HttpStatusCode.OK
        TestDataHelper.getLicenseCountByTeamId(toTeam.id) shouldBe expectedCount

        val license = TestDataHelper.getLicenseResponseByLicenseId(testData.first.first())
        license.team.id shouldBe toTeam.id
        license.team.name shouldBe toTeam.name

    }

    "POST /customer/changeLicensesTeam change multiple (3) licenses team" {
        val teams = TestDataHelper.getTeamsByLicenseCount()
        val licensesToTransfer = 3
        require(teams.size >= 2) { "There should be at least 2 teams for this test" }
        val (fromTeam, toTeam) = teams
        val expectedCount = TestDataHelper.getLicenseCountByTeamId(toTeam.id) + licensesToTransfer


        val testData = TestDataHelper.getChangeTeamRequestFromTeamId(fromTeam.id, toTeam.id, licensesToTransfer)
        var response = client.post(AppPropsHelper.props.customerChangeLicensesTeam) {
            contentType(ContentType.Application.Json)
            setBody(testData.second)
        }
        response.status shouldBe HttpStatusCode.OK
        TestDataHelper.getLicenseCountByTeamId(toTeam.id) shouldBe expectedCount

        for (item in testData.first) {
            val license = TestDataHelper.getLicenseResponseByLicenseId(item)
            license.team.id shouldBe toTeam.id
            license.team.name shouldBe toTeam.name
        }
    }

    //TODO reassign to same team

})