# Test assignment

## Description

This is set of auto-test which covered following scenarios:

- Assign license (without sending an email), i.e. /customer/licenses/assign method
- Change Team for the license, i.e. /customer/changeLicensesTeam method

## Prerequisites

- There are minimum 3 Team in organization with licenses
- Each team should have more than 3 assignable licenses to run test
- One of the team should contain only unavailable to assign licenses (e.g. expired licenses)
- You need two different accounts. First should already have JetBrains account. Second shouldn't have JetBrains account

> ⚠️ **Important:** After test you need manually revoke all assigned licenses from users in your company via web
> interface.

## Configuration

⚠️ **Important:** if you use default environment (dev) you should
configure [application-dev.yml](src/main/resources/application-dev.yml) file

⚠️ **Important:** if you use different environment you should create application-$env.yml
in [resources](src/main/resources) and pass flag `-Dapp.env=$env_name` or export environment variable export
`APP_ENV=$env_name`

### Config example

```yaml
host: account.jetbrains.com
encodedPath: /api/v1/
xCustomerCode:
xApiKey:
mainUser:
  email:
  firstName:
  lastName:
secondUser:
  email:
  firstName: Test
  lastName: Test
customerLicensesAssignPath: customer/licenses/assign
customerChangeLicensesTeam: customer/changeLicensesTeam
customerLicensesPath: customer/licenses
customerLicensesByIdPath: customer/licenses/{licenseId}
customerTeamLicensesByTeamIdPath: customer/teams/{teamId}/licenses

```

| Parameter                        | Description                                                          | Required / Default        |
|:---------------------------------|:---------------------------------------------------------------------|:--------------------------|
| host                             | JetBrains Account host                                               | Default                   |                  
| encodedPath                      | API path                                                             | Default                   |
| xCustomerCode                    | Your company unique customer number in JetBrains Account             | Required                  |
| xApiKey                          | Token generated to access API                                        | Required                  |
| mainUser                         | Main user for testing. Should have account in JetBrains Account      | Required                  |
| mainUser.email                   | Main user email                                                      | Required                  |
| mainUser.firstName               | Main user first name                                                 | Required                  |
| mainUser.lastName                | Main user last name                                                  | Required                  |
| secondUser                       | Second user for testing. Shouldn't have account in JetBrains Account | Required                  |
| secondUser.email                 | Second user email                                                    | Required                  |
| secondUser.firstName             | Second user first name                                               | Optional (default `Test`) |
| secondUser.lastName              | Second user last name                                                | Optional (default `Test`) |
| customerLicensesAssignPath       | API path to be tested                                                | Default                   |
| customerChangeLicensesTeam       | API path to be tested                                                | Default                   |
| customerLicensesPath             | Helpers path to gather licenses information                          | Default                   |
| customerLicensesByIdPath         | Helpers path to gather particular license information                | Default                   |
| customerTeamLicensesByTeamIdPath | Helpers path to gather licenses within particular team information   | Default                   |

## How to run test

1. Clone git repository
2. Navigate into the project folder
3. Run command in terminal

### bash

#### configure `application-dev.yml`

```bash
  ./gradlew test
```

#### configure your own env `application-$env.yml`

```bash
  export APP_ENV=$env
  ./gradlew test
```

### PowerShell

#### configure `application-dev.yml`

```ps
    ./gradlew test
```

#### configure your ovn env `application-$env.yml`

```ps
    $env:APP_ENV="$env"
    ./gradlew test
```

## How to check report

Navigate into build folder `build\reports\tests\test` and open `index.html` in web browser to check test results.
