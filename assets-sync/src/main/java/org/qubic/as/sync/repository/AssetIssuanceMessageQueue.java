package org.qubic.as.sync.repository;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

@Slf4j
public class AssetIssuanceMessageQueue {

    static final String KEY_QUEUE = "assets-service:queue:asset-issuances";
    private final ReactiveRedisTemplate<String, AssetIssuanceMessage> redisTemplate;

    public AssetIssuanceMessageQueue(ReactiveRedisTemplate<String, AssetIssuanceMessage> assetChangeTemplate) {
        this.redisTemplate = assetChangeTemplate;
    }

    public Mono<Long> send(AssetIssuanceMessage message) {
        return redisTemplate.opsForList().leftPush(KEY_QUEUE, message)
                .doOnNext(count -> log.info("Sent asset issuance message for transaction [{}]. Queue length: [{}].", message.transactionHash(), count));
    }

}
