package org.qubic.as.sync.repository;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.messages.AssetChangeMessage;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

@Slf4j
public class AssetChangeMessageQueue {

    static final String KEY_QUEUE = "assets-service:queue:asset-changes";
    private final ReactiveRedisTemplate<String, AssetChangeMessage> redisTemplate;

    public AssetChangeMessageQueue(ReactiveRedisTemplate<String, AssetChangeMessage> assetChangeTemplate) {
        this.redisTemplate = assetChangeTemplate;
    }

    public Mono<Long> send(AssetChangeMessage message) {
        return redisTemplate.opsForList().leftPush(KEY_QUEUE, message)
                .doOnNext(count -> log.info("Put asset change message for transaction [{}] into queue. Queue length: [{}].", message.transactionHash(), count));
    }

}
