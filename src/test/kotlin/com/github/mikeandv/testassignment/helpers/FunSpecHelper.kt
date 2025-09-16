package com.github.mikeandv.testassignment.helpers

import com.github.mikeandv.testassignment.entity.ChangeTeamRequest
import com.github.mikeandv.testassignment.utils.*
import com.github.mikeandv.testassignment.utils.HttpRequestHelper.postAndReturnBody
import com.github.mikeandv.testassignment.utils.HttpRequestHelper.postAndReturnError
import com.github.mikeandv.testassignment.utils.HttpRequestHelper.postAndReturnLicense
import com.github.mikeandv.testassignment.utils.HttpRequestHelper.postAndReturnStatus
import com.github.mikeandv.testassignment.utils.TestDataHelper.getLicenseIdByTeamId
import com.github.mikeandv.testassignment.utils.TestDataHelper.getTeamsByLicenseCount
import com.github.mikeandv.testassignment.utils.TestDataHelper.rollbackChangeTeam
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.request.*
import io.ktor.http.*

object FunSpecHelper {
    private val client = HttpClientHelper.getAuthorizedClient()

    fun FunSpec.runAssignLicensePositiveTests(
        contextName: String,
        cases: List<AssignLicenseSuccessCase>
    ) {
        context(contextName) {
            withData(nameFn = { "$contextName value=${it.valueDesc}" }, ts = cases) { case ->
                val (status, actualLicense) = client.postAndReturnLicense(case.payloadFunc)

                status shouldBe HttpStatusCode.OK
                actualLicense.assignee?.email shouldBe case.expectedUser.email
                actualLicense.assignee?.name shouldContain case.expectedUser.fullName
            }
        }
    }

    fun FunSpec.runChangeLicenseFromDifferentTeam(
        contextName: String,
        case: String
    ) {
        val allRollbacks = mutableListOf<Pair<List<String>, Int>>()
        context(contextName) {
            test("$contextName $case") {
                val (fromTeamOne, fromTeamTwo, toTeam) = getTeamsByLicenseCount()
                val licenseFromTeamOne = getLicenseIdByTeamId(fromTeamOne.id)
                val licenseFromTeamTwo = getLicenseIdByTeamId(fromTeamTwo.id)
                val licenseIds = listOf(licenseFromTeamOne, licenseFromTeamTwo)

                val response = client.post(AppPropsHelper.props.customerChangeLicensesTeam) {
                    contentType(ContentType.Application.Json)
                    setBody(ChangeTeamRequest(licenseIds, toTeam.id))
                }

                response.status shouldBe HttpStatusCode.OK

                licenseIds.forEach { id ->
                    val license = TestDataHelper.getLicenseResponseByLicenseId(id)
                    license.team.id shouldBe toTeam.id
                    license.team.name shouldBe toTeam.name

                }
                allRollbacks.add(listOf(licenseFromTeamOne) to fromTeamOne.id)
                allRollbacks.add(listOf(licenseFromTeamTwo) to fromTeamTwo.id)
            }
        }
        afterSpec {
            rollbackChangeTeam(allRollbacks)
        }
    }

    fun FunSpec.runChangeLicenseTeamPositiveTests(
        contextName: String,
        cases: List<ChangeTeamSuccessCase>
    ) {
        val allRollbacks = mutableListOf<Pair<List<String>, Int>>()
        context(contextName) {
            withData(nameFn = { "$contextName value=${it.valueDesc}" }, ts = cases) { case ->
                val (status, actualLicenses, fromTeam, toTeam) = client.postAndReturnLicense(
                    case.payloadHelperFunc,
                    case.payloadFunc
                )

                status shouldBe HttpStatusCode.OK
                for (license in actualLicenses) {
                    license.team.id shouldBe toTeam.id
                    license.team.name shouldBe toTeam.name
                }
                allRollbacks += actualLicenses.map { it.licenseId } to fromTeam.id
            }
        }
        afterSpec {
            rollbackChangeTeam(allRollbacks)
        }
    }

    fun FunSpec.runErrorTests(
        contextName: String,
        cases: List<ErrorAndStatusCase>,
    ) {
        context(contextName) {
            withData(nameFn = { "$contextName value=${it.valueDesc}" }, ts = cases) { case ->
                val (status, error) = client.postAndReturnError(case.payload, case.path)

                status shouldBe case.expectedStatus
                error shouldBe case.expectedError
            }
        }
    }

    fun FunSpec.runErrorWithContainsTest(
        contextName: String,
        cases: List<AssignLicenseErrorExtendedCase>,
    ) {
        context(contextName) {
            withData(nameFn = { "$contextName value=${it.valueDesc}" }, ts = cases) { case ->
                val (status, error) = client.postAndReturnError(case.payloadFunc)

                status shouldBe case.expectedStatus
                error.code shouldBe case.expectedError.code
                error.description shouldStartWith case.expectedError.description
            }
        }
    }

    fun FunSpec.runStatusOnlyTests(
        contextName: String,
        cases: List<StatusOnlyCase>
    ) {
        context(contextName) {
            withData(nameFn = { "$contextName value=${it.valueDesc}" }, ts = cases) { case ->
                val status = client.postAndReturnStatus(case.payload, case.path)
                status shouldBe case.expectedStatus
            }
        }
    }

    fun FunSpec.runResponseOnlyTests(
        contextName: String,
        cases: List<StatusAndBodyCase>
    ) {
        context(contextName) {
            withData(nameFn = { "$contextName value=${it.valueDesc}" }, ts = cases) { case ->
                val (status, body) = client.postAndReturnBody(case.payload, case.path)
                status shouldBe case.expectedStatus
                body shouldBe case.expectedBody

            }
        }
    }
}
