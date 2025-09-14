package com.github.mikeandv.testassignment.positive

import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runChangeLicenseFromDifferentTeam
import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runChangeLicenseTeamPositiveTests
import com.github.mikeandv.testassignment.utils.ChangeTeamSuccessCase
import com.github.mikeandv.testassignment.utils.TestDataHelper.getChangeTeamRequestFromTeamId
import com.github.mikeandv.testassignment.utils.TestDataHelper.getSameTeamsByLicenseCount
import com.github.mikeandv.testassignment.utils.TestDataHelper.getTeamsByLicenseCount
import io.kotest.core.spec.style.FunSpec

class PostChangeLicenseTeamPositiveTests : FunSpec({

    runChangeLicenseTeamPositiveTests(
        "Change license team",
        listOf(
            ChangeTeamSuccessCase(
                "single",
                { getTeamsByLicenseCount() },
                { fromTeam, toTeam -> getChangeTeamRequestFromTeamId(fromTeam, toTeam) },
            ),
            ChangeTeamSuccessCase(
                "multiple",
                { getTeamsByLicenseCount() },
                { fromTeam, toTeam -> getChangeTeamRequestFromTeamId(fromTeam, toTeam, 3) },
            ),
        )
    )

    runChangeLicenseTeamPositiveTests(
        "Change license team to the same team",
        listOf(
            ChangeTeamSuccessCase(
                "single",
                { getSameTeamsByLicenseCount() },
                { fromTeam, toTeam -> getChangeTeamRequestFromTeamId(fromTeam, toTeam) },
            ),
            ChangeTeamSuccessCase(
                "multiple",
                { getSameTeamsByLicenseCount() },
                { fromTeam, toTeam -> getChangeTeamRequestFromTeamId(fromTeam, toTeam, 3) },
            ),
        )
    )

    runChangeLicenseFromDifferentTeam(
        "Assign license from",
        "different teams"

    )
})