# Asset Owners Service API

Service that provides endpoints to retrieve owners of assets.

## Installation

You need Postgresql and Redis installed.

## Configuration Properties

You can use all spring boot configuration properties. For additional configuration properties check out the
see the [application.properties](src/main/resources/application.properties) file in the source code.

### General

Set server port. Example:

```properties
server.port=8083
```

### Redis

Defaults point to localhost and the default redis port without authorization. You can change that with the following
properties:

```properties
spring.data.redis.port=...
spring.data.redis.host=...
spring.data.redis.password=...
```

### Postgresql

You need to configure the database connection. Make sure that user and schema are created before startup. The flyway
migrations will update the database.

#### Create database user and schema

Example with user `aos` and database `aos`:

```postgresql
CREATE USER aos WITH PASSWORD '<enter-your-password-here>';
CREATE DATABASE aos OWNER aos;
```

#### Configure database connections

```properties
# database (application)
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...

# flyway database migrations
spring.flyway.url=...
spring.flyway.user=...
spring.flyway.password=...
```

### Schedulers

Schedulers can be configured with cron syntax or disabled with `-`. Schedulers are disabled by default.

```properties
scheduler.sync.cron=*/15 * * * * * # every 15 seconds
scheduler.import.universe.cron=0 */1 * * * * # every minute
```

The sync scheduler checks the redis queue for asset events. The import scheduler checks the import folder for
new universe files (Attention: import replaces the complete data. It does not replay the events that might have happened
since epoch change.).

### Endpoints

If you want to access secured endpoints (like prometheus metrics) you need to authenticate and configure user information.

```properties
spring.security.user.name=...
spring.security.user.password=...
```

## API

See [OpenAPI descriptions](api-docs.yaml).
