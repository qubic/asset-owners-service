package org.qubic.as.sync.config;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.qubic.as.sync.repository.AssetChangeMessageQueue;
import org.qubic.as.sync.repository.AssetIssuanceMessageQueue;
import org.qubic.as.sync.repository.TickRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RepositoryConfiguration {

    @Bean
    public ReactiveRedisTemplate<String, AssetChangeMessage> assetChangeMessageTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<AssetChangeMessage> serializer = new Jackson2JsonRedisSerializer<>(AssetChangeMessage.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, AssetChangeMessage> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, AssetChangeMessage> context = builder
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, AssetIssuanceMessage> assetIssuanceMessageTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<AssetIssuanceMessage> serializer = new Jackson2JsonRedisSerializer<>(AssetIssuanceMessage.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, AssetIssuanceMessage> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, AssetIssuanceMessage> context = builder
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Bean
    AssetChangeMessageQueue assetChangeMessageQueue(ReactiveRedisTemplate<String, AssetChangeMessage> redisTemplate) {
        return new AssetChangeMessageQueue(redisTemplate);
    }

    @Bean
    AssetIssuanceMessageQueue assetIssuanceMessageQueue(ReactiveRedisTemplate<String, AssetIssuanceMessage> redisTemplate) {
        return new AssetIssuanceMessageQueue(redisTemplate);
    }

    @Bean
    TickRepository tickRepository(ReactiveStringRedisTemplate redisStringTemplate, Environment environment) {
        long keepTicks = environment.getProperty("repository.ticks.keep", Long.class, 1_000_000L);
        log.info("Number of stored processed tick numbers: {}", keepTicks);
        return new TickRepository(redisStringTemplate, keepTicks);
    }


}
