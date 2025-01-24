package org.qubic.as.sync.repository;

import org.junit.jupiter.api.Test;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.sync.AbstractRedisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import static org.qubic.as.sync.repository.AssetChangeMessageQueue.KEY_QUEUE;

class AssetChangeMessageQueueSpringIT extends AbstractRedisTest {

    @Autowired
    private AssetChangeMessageQueue messageSender;

    @Autowired
    private ReactiveRedisTemplate<String, AssetChangeMessage> redisTemplate;

    @Test
    void send_thenPutIntoQueue() {
        AssetChangeMessage message = new AssetChangeMessage("source", "destination", "issuer", "assetName", 1, "transactionHash", 2, 3);

        StepVerifier.create(messageSender.send(message))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(redisTemplate.opsForList().rightPop(KEY_QUEUE))
                .expectNext(message)
                .verifyComplete();
    }

}