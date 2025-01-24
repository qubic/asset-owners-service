package org.qubic.as.sync.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.qubic.as.sync.AbstractRedisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(properties = {"""
    spring.data.redis.port=16379
    repository.ticks.keep=10
"""})
class TickRepositorySpringIT extends AbstractRedisTest {

    @Autowired
    private TickRepository tickRepository;

    @Autowired
    private ReactiveStringRedisTemplate redisStringTemplate;

    @Test
    void setCurrentTick() {
        StepVerifier.create(tickRepository.setLatestSyncedTick(42))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(redisStringTemplate.opsForValue().get("assets-service:tick:synced"))
                .expectNext("42")
                .verifyComplete();
    }

    @Test
    void getCurrentTick() {
        Mono<Long> latestTick = tickRepository.setLatestSyncedTick(42)
                .then(tickRepository.getLatestSyncedTick());

        StepVerifier.create(latestTick)
                .expectNext(42L)
                .verifyComplete();
    }

    @Test
    void addToProcessedTicks() {
        StepVerifier.create(tickRepository.addToProcessedTicks(123))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(tickRepository.addToProcessedTicks(123))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(redisStringTemplate.opsForZSet().score("assets-service:ticks:processed", "123"))
                .expectNext((double) 123)
                .verifyComplete();
    }

    @Test
    void addToProcessedTicks_givenMaxKeepCountReached_thenDisposeOldEntries() {
        // this test needs numbers larger than the other tests because disposing happens by rank and higher ticks
        // always have higher rank

        // add 20 ticks
        for (int i = 1000; i <= 1020; i++) {
            tickRepository.addToProcessedTicks(i).block();
            assertThat(tickRepository.isProcessedTick(i).block()).isTrue();
        }

        // first 10 ticks are not stored anymore (smaller keep count)
        for (int i = 1000; i <= 1010; i++) {
            log.info("Test that tick [{}] is not stored anymore.", i);
            assertThat(tickRepository.isProcessedTick(i).block()).isFalse();
        }

        // last 10 ticks are still stored
        for (int i = 1011; i <= 1020; i++) {
            log.info("Test that tick [{}] is still stored.", i);
            assertThat(tickRepository.isProcessedTick(i).block()).isTrue();
        }
    }

    @Test
    void isProcessedTick() {
        tickRepository.addToProcessedTicks(42L)
                .then(tickRepository.addToProcessedTicks(43L))
                .block();

        StepVerifier.create(tickRepository.isProcessedTick(42L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(tickRepository.isProcessedTick(43L))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(tickRepository.isProcessedTick(666L))
                .expectNext(false)
                .verifyComplete();
    }


}