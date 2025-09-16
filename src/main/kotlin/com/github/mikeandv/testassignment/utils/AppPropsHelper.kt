package com.github.mikeandv.testassignment.utils

import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException


object AppPropsHelper {
    val props: AppProps

    init {
        val env = System.getProperty("app.env")
            ?: System.getenv("APP_ENV")
            ?: "dev" // env by default
        val fileName = "application-$env.yml"

        val yaml = Yaml()
        val inputStream = ClassLoader.getSystemResourceAsStream(fileName)
            ?: throw FileNotFoundException("$fileName not found!")

        val yamlMap: Map<String, Any> = yaml.load(inputStream)

        fun requireProp(value: String?, name: String) =
            value ?: error("Missing required property: $name. Pass it via -D$name=value or set in $fileName")

        val mainUserMap = yamlMap["mainUser"] as? Map<*, *>
        val secondUserMap = yamlMap["secondUser"] as? Map<*, *>

        val final = AppProps(
            host = requireProp(yamlMap["host"] as? String?, "host"),
            encodedPath = requireProp(yamlMap["encodedPath"] as? String?, "encodedPath"),
            xCustomerCode = requireProp(
                System.getProperty("xCustomerCode") ?: yamlMap["xCustomerCode"] as? String?,
                "xCustomerCode"
            ),
            xApiKey = requireProp(System.getProperty("xApiKey") ?: yamlMap["xApiKey"] as? String?, "xApiKey"),
            mainUser = AppProps.User(
                email = requireProp(
                    System.getProperty("mainUser.email")
                        ?: mainUserMap?.get("email") as? String?,
                    "mainUser.email"
                ),
                firstName = requireProp(
                    System.getProperty("mainUser.firstName")
                        ?: mainUserMap?.get("firstName") as? String?,
                    "mainUser.firstName"
                ),
                lastName = requireProp(
                    System.getProperty("mainUser.lastName")
                        ?: mainUserMap?.get("lastName") as? String?,
                    "mainUser.lastName"
                )
            ),
            secondUser = AppProps.User(
                email = requireProp(
                    System.getProperty("secondUser.email")
                        ?: secondUserMap?.get("email") as? String?,
                    "secondUser.email"
                ),
                firstName = requireProp(
                    System.getProperty("secondUser.firstName")
                        ?: secondUserMap?.get("firstName") as? String?,
                    "secondUser.firstName"
                ),
                lastName = requireProp(
                    System.getProperty("secondUser.lastName")
                        ?: secondUserMap?.get("lastName") as? String?,
                    "secondUser.lastName"
                )
            ),
            customerLicensesAssignPath = requireProp(
                yamlMap["customerLicensesAssignPath"] as? String?,
                "customerLicensesAssignPath"
            ),
            customerLicensesPath = requireProp(yamlMap["customerLicensesPath"] as? String?, "customerLicensesPath"),
            customerLicensesByIdPath = requireProp(
                yamlMap["customerLicensesByIdPath"] as? String?,
                "customerLicensesByIdPath"
            ),
            customerTeamLicensesByTeamIdPath = requireProp(
                yamlMap["customerTeamLicensesByTeamIdPath"] as? String?, "customerTeamLicensesByTeamIdPath"
            ),
            customerChangeLicensesTeam = requireProp(
                yamlMap["customerChangeLicensesTeam"] as? String?,
                "customerChangeLicensesTeam"
            )
        )

        props = final
    }

    data class AppProps(
        val host: String,
        val encodedPath: String,
        val xCustomerCode: String,
        val xApiKey: String,
        val mainUser: User,
        val secondUser: User,
        val customerLicensesAssignPath: String,
        val customerLicensesPath: String,
        val customerLicensesByIdPath: String,
        val customerTeamLicensesByTeamIdPath: String,
        val customerChangeLicensesTeam: String
    ) {
        data class User(
            val email: String,
            val firstName: String,
            val lastName: String,
        ) {
            val fullName: String get() = "$firstName $lastName"
            fun maskName(): User =
                copy(
//                    firstName = firstName.take(2) + "*".repeat(11),
//                    lastName = lastName.take(2) + "*".repeat(10)
                    firstName = "***",
                    lastName = ""
                )
        }

        val maskedSecondUser: User get() = secondUser.maskName()
    }
}






