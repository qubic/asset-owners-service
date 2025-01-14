package org.qubic.aos.api.config;

import lombok.extern.slf4j.Slf4j;
import org.qubic.aos.api.redis.ApplicationCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableCaching
@Configuration
public class RedisConfiguration {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, Environment environment) {

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        String[] caches = environment.getProperty("qx.caches", String[].class, new String[0]);
        for (String cache : caches) {
            Duration ttl = environment.getProperty(String.format("qx.cache.%s.ttl", cache), Duration.class);
            if (ttl == null) {
                log.warn("Missing cache configuration for [{}]", cache);
            } else {
                log.info("Overriding defaults for cache [{}]. TTL: [{}]", cache, ttl);
                cacheConfigurations.put(cache, RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttl));
            }
        }

        Duration defaultTtl = environment.getRequiredProperty("aos.cache.default.ttl", Duration.class);
        log.info("Default cache ttl: [{}]", defaultTtl);
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(defaultTtl)
                        .disableCachingNullValues())
                .build();

    }

    @Bean
    ApplicationCacheManager serviceCacheManager(RedisCacheManager cacheManager) {
        return new ApplicationCacheManager(cacheManager);
    }

}

