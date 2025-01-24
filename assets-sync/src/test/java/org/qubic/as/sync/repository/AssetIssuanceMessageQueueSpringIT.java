package org.qubic.as.sync.repository;

import org.junit.jupiter.api.Test;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.qubic.as.sync.AbstractRedisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import static org.qubic.as.sync.repository.AssetChangeMessageQueue.KEY_QUEUE;

class AssetIssuanceMessageQueueSpringIT extends AbstractRedisTest {

    @Autowired
    private AssetIssuanceMessageQueue messageSender;

    @Autowired
    private ReactiveRedisTemplate<String, AssetIssuanceMessage> redisTemplate;

    @Test
    void send_thenPutIntoQueue() {
        AssetIssuanceMessage message = new AssetIssuanceMessage("issuer", "assetName", 1, "transactionHash", 2);

        StepVerifier.create(messageSender.send(message))
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(redisTemplate.opsForList().rightPop(KEY_QUEUE))
                .expectNext(message)
                .verifyComplete();
    }

}