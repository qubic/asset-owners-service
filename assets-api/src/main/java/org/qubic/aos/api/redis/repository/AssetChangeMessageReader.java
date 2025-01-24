package org.qubic.aos.api.redis.repository;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.messages.AssetChangeMessage;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class AssetChangeMessageReader implements QueueProcessingRepository<AssetChangeMessage> {

    static final String KEY_QUEUE_RECEIVE = "assets-service:queue:asset-changes";
    static final String KEY_QUEUE_PROCESS = "assets-service:queue:asset-changes:processing";
    static final String KEY_QUEUE_ERRORS = "assets-service:queue:asset-changes:errors";

    private final RedisTemplate<String, AssetChangeMessage> redisTemplate;

    public AssetChangeMessageReader(RedisTemplate<String, AssetChangeMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public AssetChangeMessage readFromQueue() {
        return redisTemplate.opsForList().rightPopAndLeftPush(KEY_QUEUE_RECEIVE, KEY_QUEUE_PROCESS);
    }

    @Override
    public Long removeFromProcessingQueue(AssetChangeMessage message) {
        return redisTemplate.opsForList().remove(KEY_QUEUE_PROCESS, 1, message);
    }

    @Override
    public Long pushIntoErrorsQueue(AssetChangeMessage message) {
        log.warn("Pushing into errors queue: {}", message);
        return redisTemplate.opsForList().leftPush(KEY_QUEUE_ERRORS, message);
    }
}
