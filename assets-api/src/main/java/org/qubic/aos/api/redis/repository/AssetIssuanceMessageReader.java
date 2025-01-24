package org.qubic.aos.api.redis.repository;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class AssetIssuanceMessageReader implements QueueProcessingRepository<AssetIssuanceMessage> {

    static final String KEY_QUEUE_RECEIVE = "assets-service:queue:asset-issuances";
    static final String KEY_QUEUE_PROCESS = "assets-service:queue:asset-issuances:processing";
    static final String KEY_QUEUE_ERRORS = "assets-service:queue:asset-issuances:errors";

    private final RedisTemplate<String, AssetIssuanceMessage> redisTemplate;

    public AssetIssuanceMessageReader(RedisTemplate<String, AssetIssuanceMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public AssetIssuanceMessage readFromQueue() {
        return redisTemplate.opsForList().rightPopAndLeftPush(KEY_QUEUE_RECEIVE, KEY_QUEUE_PROCESS);
    }

    @Override
    public Long removeFromProcessingQueue(AssetIssuanceMessage message) {
        return redisTemplate.opsForList().remove(KEY_QUEUE_PROCESS, 1, message);
    }

    @Override
    public Long pushIntoErrorsQueue(AssetIssuanceMessage message) {
        log.warn("Pushing into errors queue: {}", message);
        return redisTemplate.opsForList().leftPush(KEY_QUEUE_ERRORS, message);
    }
}
