package org.qubic.aos.api.config;

import lombok.extern.slf4j.Slf4j;
import org.qubic.aos.api.redis.AssetsCacheManager;
import org.qubic.aos.api.redis.repository.AssetChangeMessageReader;
import org.qubic.aos.api.redis.repository.AssetIssuanceMessageReader;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableCaching
@Configuration
public class RedisConfiguration {

    @Bean
    RedisTemplate<String, AssetIssuanceMessage> assetIssuanceMessageTemplate(RedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<AssetIssuanceMessage> jsonSerializer = new Jackson2JsonRedisSerializer<>(AssetIssuanceMessage.class);
        RedisTemplate<String, AssetIssuanceMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jsonSerializer);
        return template;
    }

    @Bean
    RedisTemplate<String, AssetChangeMessage> assetChangeMessageTemplate(RedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<AssetChangeMessage> jsonSerializer = new Jackson2JsonRedisSerializer<>(AssetChangeMessage.class);
        RedisTemplate<String, AssetChangeMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jsonSerializer);
        return template;
    }

    @Bean
    AssetIssuanceMessageReader assetIssuanceMessageReader(RedisTemplate<String, AssetIssuanceMessage> assetIssuanceMessageTemplate) {
        return new AssetIssuanceMessageReader(assetIssuanceMessageTemplate);
    }

    @Bean
    AssetChangeMessageReader assetChangeMessageReader(RedisTemplate<String, AssetChangeMessage> assetChangeMessageTemplate) {
        return new AssetChangeMessageReader(assetChangeMessageTemplate);
    }

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
    AssetsCacheManager serviceCacheManager(RedisCacheManager cacheManager) {
        return new AssetsCacheManager(cacheManager);
    }

}

