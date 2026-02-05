Individuals Tax Liability Adjustments API
========================

The Individuals Tax Liability Adjustments API allows a developer to retrieve, create or amend, and delete data
relating to tax liability adjustments.

## Requirements

- Scala 3.5.x
- Java 11
- sbt 1.10.x
- [Service Manager 2](https://github.com/hmrc/sm2)

## Running the microservice

Run from the console using: `sbt run` (starts on port 7767 by default)

Start the service manager profile: `sm2 --start MTDFB_INDIVIDUALS_TAX_LIABILITY_ADJUSTMENTS`

## Running test

Run unit tests: `sbt test`

Run integration tests: `sbt it/test`

## Viewing Open API Spec (OAS) docs

To view documentation locally ensure the Individuals Tax Liability Adjustments API is running, and run api-documentation-frontend:
`./run_local_with_dependencies.sh`

Then go to http://localhost:9680/api-documentation/docs/openapi/preview and use this port and version:
`http://localhost:7767/api/conf/1.0/application.yaml`

## Changelog

You can see our changelog [here](https://github.com/hmrc/income-tax-mtd-changelog)

## Support and Reporting Issues
