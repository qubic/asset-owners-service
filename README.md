# Asset Owners Service

Provides endpoints for looking up asset owners.

There are two modules:

* [assets-api](./assets-api/README.md) ... api endpoints
* [assets-sync](./assets-sync/README.md) ... job that retrieves the data from the integration layer

## Prerequisites

* Java 21
* Maven

## Build

`mvn clean verify`

To run integration tests use `verfy` target.

This builds all modules. The artifacts are available in the `target/` folders of
modules.

## Run

Run applications with `java -jar <jar-file-of-application>`.

## Configuration

For configuration, it is recommended to use an `application.properties` file.
See `application.properties` file in modules `src/main/resources` folder for available
configuration properties.
See [Spring Documentation](https://docs.spring.io/spring-boot/reference/features/external-config.html) for available
possibilities of configuring.
