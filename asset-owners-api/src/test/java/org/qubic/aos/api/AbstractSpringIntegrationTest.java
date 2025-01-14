package org.qubic.aos.api;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Objects;


@ImportTestcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(properties = """
    spring.data.redis.port=26379
    spring.cache.type=NONE
""")
@Slf4j
public abstract class AbstractSpringIntegrationTest {

    // test database
    @ServiceConnection
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.13");

    // test redis db
    @ServiceConnection
    static RedisContainer redisContainer = new RedisContainer("redis:7.0.15");

    // caching
    @Autowired
    protected CacheManager cacheManager;

    protected void evictAllCaches() {
        for(String name : cacheManager.getCacheNames()){
            log.info("Evicting cache [{}].", name);
            Objects.requireNonNull(cacheManager.getCache(name)).clear();
        }
    }

}
