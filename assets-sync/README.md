# Asset Owners Sync Job

## Installation

### Redis

Redis is used for storing the sync status and filling queues that pass qx data to consumers.

## Configuration Properties

### General

Set server port. Example:

```properties
server.port=8084
```

### Redis

Defaults point to localhost and the default redis port without authorization. You can change that with the following
properties:

```properties
spring.data.redis.port=...
spring.data.redis.host=...
spring.data.redis.password=...
```

### Endpoints

If you want to access secured endpoints (like prometheus metrics) you need to authenticate and configure user information.

```properties
spring.security.user.name=...
spring.security.user.password=...
```

