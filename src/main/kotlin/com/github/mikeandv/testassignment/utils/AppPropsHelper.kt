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
        val firstName = yamlMap["firstName"] as? String ?: error("Missing 'firstName' in $fileName")
        val lastName = yamlMap["lastName"] as? String ?: error("Missing 'lastName' in $fileName")
        props = AppProps(
            host = yamlMap["host"] as? String ?: error("Missing 'host' $fileName"),
            encodedPath = yamlMap["encodedPath"] as? String ?: error("Missing 'encodedPath' $fileName"),
            xCustomerCode = yamlMap["x-customer-code"] as? String ?: error("Missing 'x-customer-code' $fileName"),
            xApiKey = yamlMap["x-api-key"] as? String ?: error("Missing 'x-api-key' in $fileName"),
            firstName = firstName,
            lastName = lastName,
            fullName = "$firstName $lastName",
            email = yamlMap["email"] as? String ?: error("Missing 'email' in $fileName"),
            fakeFirstName = yamlMap["fakeFirstName"] as? String ?: error("Missing 'fakeFirstName' in $fileName"),
            fakeLastName = yamlMap["fakeLastName"] as? String ?: error("Missing 'fakeLastName' in $fileName"),
            fakeEmail = yamlMap["fakeEmail"] as? String ?: error("Missing 'fakeEmail' in $fileName"),
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
        val firstName: String,
        val lastName: String,
        val fullName: String,
        val email: String,
        val fakeFirstName: String,
        val fakeLastName: String,
        val fakeEmail: String,
        val customerLicensesAssignPath: String,
        val customerLicensesPath: String,
        val customerLicensesByIdPath: String,
        val customerTeamLicensesByTeamIdPath: String,
        val customerChangeLicensesTeam: String
    )
}





