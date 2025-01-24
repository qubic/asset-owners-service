package org.qubic.as.sync.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

@SuppressWarnings("SameParameterValue")
@Slf4j
public class TickRepository {

    public static final String KEY_TICK_SYNCED_LATEST = "assets-service:tick:synced"; // key value
    public static final String KEY_TICKS_PROCESSED = "assets-service:ticks:processed"; // set

    private final Range<Long> disposeRange;
    private final ReactiveStringRedisTemplate redisStringTemplate;

    public TickRepository(ReactiveStringRedisTemplate redisStringTemplate, long keepCount) {
        this.redisStringTemplate = redisStringTemplate;
        // dispose from 0 to min keep range (counted from end)
        this.disposeRange = Range.closed(0L, -(keepCount+1));
    }

    public Mono<Long> getLatestSyncedTick() {
        return getValue(KEY_TICK_SYNCED_LATEST)
                .defaultIfEmpty("0")
                .map(this::toLongValue);
    }

    public Mono<Boolean> setLatestSyncedTick(long latestSyncedBlock) {
        return setValue(KEY_TICK_SYNCED_LATEST, String.valueOf(latestSyncedBlock));
    }

    public Mono<Boolean> isProcessedTick(long tickNumber) {
        return redisStringTemplate.opsForZSet()
                .score(KEY_TICKS_PROCESSED, String.valueOf(tickNumber)) // returns empty if not found

                .defaultIfEmpty((double) -1)
                .map(score -> score != null && score > 0);
    }

    public Mono<Boolean> addToProcessedTicks(long tickNumber) {
        return addToZSet(KEY_TICKS_PROCESSED, tickNumber)
                .flatMap(x -> removeFromZSet(KEY_TICKS_PROCESSED, disposeRange)
                        .map(y -> x)); // return info from add to z set
    }

    private Mono<Long> removeFromZSet(String indexKey, Range<Long> range) {
        return redisStringTemplate.opsForZSet()
                .removeRange(indexKey, range)
                .doOnNext(count -> log.debug("Removed [{}] entries from index [{}].", count, indexKey)); // clean index
    }

    private Mono<Boolean> addToZSet(String key, long value) {
        return redisStringTemplate.opsForZSet()
                .add(key, String.valueOf(value), (double) value)
                .doOnNext(added -> log.debug("Added/updated [{}] in zset [{}]: {}", value, key, added));
    }

    private Mono<Boolean> setValue(String key, String value) {
        return redisStringTemplate
                .opsForValue()
                .set(key, value)
                .doOnSuccess(success -> log.debug("Set key [{}] to [{}].", key, value))
                .doOnError(t -> log.error("Updating key [{}] to [{}] failed: {}", key, value, t.toString()));
    }

    private Mono<String> getValue(String key) {
        return redisStringTemplate
                .opsForValue()
                .get(key)
                .doOnNext(value -> log.debug("Retrieved [{}]: [{}].", key, value));
    }

    private Long toLongValue(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            log.error(nfe.toString(), nfe);
            return 0L;
        }
    }

}
