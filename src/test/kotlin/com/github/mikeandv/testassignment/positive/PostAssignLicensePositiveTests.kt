package com.github.mikeandv.testassignment.positive

import com.github.mikeandv.testassignment.helpers.FunSpecHelper.runAssignLicensePositiveTests
import com.github.mikeandv.testassignment.utils.AppPropsHelper
import com.github.mikeandv.testassignment.utils.AssignLicenseSuccessCase
import com.github.mikeandv.testassignment.utils.TestDataHelper.getAssignLicenseRequestFromTeam
import com.github.mikeandv.testassignment.utils.TestDataHelper.getAssignLicenseRequestWithLicenseId
import io.kotest.core.spec.style.FunSpec

class PostAssignLicensePositiveTests : FunSpec({

    runAssignLicensePositiveTests(
        "Assign single license by id",
        listOf(
            AssignLicenseSuccessCase(
                "to a user with account",
                { getAssignLicenseRequestWithLicenseId(AppPropsHelper.props.mainUser) },
                AppPropsHelper.props.mainUser,
            ),
            AssignLicenseSuccessCase(
                "to a user without account",
                { getAssignLicenseRequestWithLicenseId(AppPropsHelper.props.secondUser) },
                AppPropsHelper.props.maskedSecondUser,
            ),
            AssignLicenseSuccessCase(
                "to a user with account but different name and surname",
                {
                    getAssignLicenseRequestWithLicenseId(
                        AppPropsHelper.props.mainUser.copy(
                            email = AppPropsHelper.props.mainUser.email,
                            firstName = "Test",
                            lastName = "Test"
                        )
                    )
                },
                AppPropsHelper.props.mainUser,
            ),
        )
    )

    runAssignLicensePositiveTests(
        "Assign single license from team",
        listOf(
            AssignLicenseSuccessCase(
                "to a user with account",
                {
                    getAssignLicenseRequestFromTeam(AppPropsHelper.props.mainUser)
                },
                AppPropsHelper.props.mainUser,
            ),
            AssignLicenseSuccessCase(
                "to a user without account",
                {
                    getAssignLicenseRequestFromTeam(AppPropsHelper.props.secondUser)
                },
                AppPropsHelper.props.maskedSecondUser,
            ),
            AssignLicenseSuccessCase(
                "to a user with account but different name and surname",
                {
                    getAssignLicenseRequestFromTeam(
                        AppPropsHelper.props.mainUser.copy(
                            email = AppPropsHelper.props.mainUser.email,
                            firstName = "Test",
                            lastName = "Test"
                        ),
                    )
                },
                AppPropsHelper.props.mainUser,
            )
        )
    )
})