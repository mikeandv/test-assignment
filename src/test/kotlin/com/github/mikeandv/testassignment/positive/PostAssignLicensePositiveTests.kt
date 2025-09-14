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
                {
                    getAssignLicenseRequestWithLicenseId(
                        AppPropsHelper.props.mainUser.email,
                        AppPropsHelper.props.mainUser.firstName,
                        AppPropsHelper.props.mainUser.lastName,
                    )
                },
                AppPropsHelper.props.mainUser,
            ),
            AssignLicenseSuccessCase(
                "to a user without account",
                {
                    getAssignLicenseRequestWithLicenseId(
                        AppPropsHelper.props.secondUser.email,
                        AppPropsHelper.props.secondUser.firstName,
                        AppPropsHelper.props.secondUser.lastName,
                    )
                },
                AppPropsHelper.props.maskedSecondUser,
            ),
            AssignLicenseSuccessCase(
                "to a user with account but different name and surname",
                {
                    getAssignLicenseRequestWithLicenseId(
                        AppPropsHelper.props.mainUser.email,
                        "Test",
                        "Test",
                    )
                },
                AppPropsHelper.props.mainUser,
            ),
            AssignLicenseSuccessCase(
                "reassign to a same user",
                {
                    getAssignLicenseRequestWithLicenseId(
                        AppPropsHelper.props.mainUser.email,
                        AppPropsHelper.props.mainUser.firstName,
                        AppPropsHelper.props.mainUser.lastName,
                    )
                },
                AppPropsHelper.props.mainUser,
            )
        )
    )

    runAssignLicensePositiveTests(
        "Assign single license from team",
        listOf(
            AssignLicenseSuccessCase(
                "to a user with account",
                {
                    getAssignLicenseRequestFromTeam(
                        AppPropsHelper.props.mainUser.email,
                        AppPropsHelper.props.mainUser.firstName,
                        AppPropsHelper.props.mainUser.lastName,
                    )
                },
                AppPropsHelper.props.mainUser,
            ),
            AssignLicenseSuccessCase(
                "to a user without account",
                {
                    getAssignLicenseRequestFromTeam(
                        AppPropsHelper.props.secondUser.email,
                        AppPropsHelper.props.secondUser.firstName,
                        AppPropsHelper.props.secondUser.lastName,
                    )
                },
                AppPropsHelper.props.maskedSecondUser,
            ),
            AssignLicenseSuccessCase(
                "to a user with account but different name and surname",
                {
                    getAssignLicenseRequestFromTeam(
                        AppPropsHelper.props.mainUser.email,
                        "Test",
                        "Test",
                    )
                },
                AppPropsHelper.props.mainUser,
            )
        )
    )

//TODO Corner case reassign to same person
//TODO single / assign to person account with wrong name and surname, full name should be unchanged
//TODO single / assign to person without account with name and surname, full name should be name + last name
//TODO from team / assign to person account with wrong name and surname, full name should be unchanged
//TODO from team / assign to person without account with name and surname, full name should be name + last name

})