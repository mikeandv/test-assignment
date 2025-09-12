package com.github.mikeandv.testassignment.utils

import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException


object AppPropsHelper {
    val props: AppProps

    init {
        val env = System.getProperty("app.env")
            ?: System.getenv("APP_ENV")
            ?: "dev2" // env by default
        val fileName = "application-$env.yml"

        val yaml = Yaml()
        val inputStream = ClassLoader.getSystemResourceAsStream(fileName)
            ?: throw FileNotFoundException("$fileName not found!")


        val yamlMap: Map<String, Any> = yaml.load(inputStream)
        val mainUserFirstName =
            yamlMap["mainUserFirstName"] as? String ?: error("Missing 'mainUserFirstName' in $fileName")
        val mainUserLastName =
            yamlMap["mainUserLastName"] as? String ?: error("Missing 'mainUserLastName' in $fileName")
        val secondUserFirstName =
            yamlMap["secondUserFirstName"] as? String ?: error("Missing 'secondUserFirstName' in $fileName")
        val secondUserLastName =
            yamlMap["secondUserLastName"] as? String ?: error("Missing 'secondUserLastName' in $fileName")
        props = AppProps(
            host = yamlMap["host"] as? String ?: error("Missing 'host' $fileName"),
            encodedPath = yamlMap["encodedPath"] as? String ?: error("Missing 'encodedPath' $fileName"),
            xCustomerCode = yamlMap["x-customer-code"] as? String ?: error("Missing 'x-customer-code' $fileName"),
            xApiKey = yamlMap["x-api-key"] as? String ?: error("Missing 'x-api-key' in $fileName"),
            mainUserFirstName = mainUserFirstName,
            mainUserLastName = mainUserLastName,
            mainUserFullName = "$mainUserFirstName $mainUserLastName",
            mainUserEmail = yamlMap["mainUserEmail"] as? String ?: error("Missing 'mainUserEmail' in $fileName"),
            secondUserFirstName = secondUserFirstName,
            secondUserLastName = secondUserLastName,
            secondUserFullName = "$secondUserFirstName $secondUserLastName",
            secondUserEmail = yamlMap["secondUserEmail"] as? String ?: error("Missing 'secondUserEmail' in $fileName"),
            customerLicensesAssignPath = yamlMap["customerLicensesAssignPath"] as? String
                ?: error("Missing 'customerLicensesAssignPath' in $fileName"),
            customerLicensesPath = yamlMap["customerLicensesPath"] as? String
                ?: error("Missing 'customerLicensesPath' in $fileName"),
            customerLicensesByIdPath = yamlMap["customerLicensesByIdPath"] as? String
                ?: error("Missing 'customerLicensesByIdPath' in $fileName"),
            customerTeamLicensesByTeamIdPath = yamlMap["customerTeamLicensesByTeamIdPath"] as? String
                ?: error("Missing 'customerTeamLicensesByTeamIdPath' in $fileName"),
            customerChangeLicensesTeam = yamlMap["customerChangeLicensesTeam"] as? String
                ?: error("Missing 'customerChangeLicensesTeam' in $fileName")
        )
    }

    data class AppProps(
        val host: String,
        val encodedPath: String,
        val xCustomerCode: String,
        val xApiKey: String,
        val mainUserFirstName: String,
        val mainUserLastName: String,
        val mainUserFullName: String,
        val mainUserEmail: String,
        val secondUserFirstName: String,
        val secondUserLastName: String,
        val secondUserFullName: String,
        val secondUserEmail: String,
        val customerLicensesAssignPath: String,
        val customerLicensesPath: String,
        val customerLicensesByIdPath: String,
        val customerTeamLicensesByTeamIdPath: String,
        val customerChangeLicensesTeam: String
    )
}





