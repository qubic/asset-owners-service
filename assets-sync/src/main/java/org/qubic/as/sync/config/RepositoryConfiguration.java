package org.qubic.as.sync.config;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.repository.TickRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Slf4j
@Configuration
public class RepositoryConfiguration {

    @Bean
    TickRepository tickRepository(ReactiveStringRedisTemplate redisStringTemplate, Environment environment) {
        long keepTicks = environment.getProperty("repository.ticks.keep", Long.class, 1_000_000L);
        log.info("Number of stored processed tick numbers: {}", keepTicks);
        return new TickRepository(redisStringTemplate, keepTicks);
    }


}
