package org.qubic.aos.api.config;

import lombok.extern.slf4j.Slf4j;
import org.qubic.aos.api.redis.AssetCacheManager;
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
        String[] caches = environment.getProperty("aos.caches", String[].class, new String[0]);
        for (String cache : caches) {
            Duration customTtl = environment.getRequiredProperty(String.format("aos.cache.%s.ttl", cache), Duration.class);
            String cacheName = environment.getRequiredProperty(String.format("aos.cache.%s.name", cache), String.class);
            log.info("Overriding defaults for cache [{}]. TTL: [{}]", cacheName, customTtl);
            cacheConfigurations.put(cacheName, RedisCacheConfiguration.defaultCacheConfig().entryTtl(customTtl));
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
    AssetCacheManager serviceCacheManager(RedisCacheManager cacheManager) {
        return new AssetCacheManager(cacheManager);
    }

}

