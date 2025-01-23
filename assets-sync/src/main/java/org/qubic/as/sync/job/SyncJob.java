package org.qubic.as.sync.job;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.adapter.EventApiService;
import org.qubic.as.sync.domain.TickInfo;
import org.qubic.as.sync.repository.TickRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
public class SyncJob {

    private final CoreApiService coreService;
    private final EventApiService eventService;
    private final TickRepository tickRepository;

    public SyncJob(CoreApiService coreService, EventApiService eventService, TickRepository tickRepository) {
        this.coreService = coreService;
        this.eventService = eventService;
        this.tickRepository = tickRepository;
    }

    public Mono<Long> sync() {
        return getLatestAvailableTick()
                .flatMap(this::calculateStartAndEndTick)
                .flatMapMany(this::calculateSyncRange)
                .doOnNext(tickNumber -> log.debug("Syncing tick [{}].", tickNumber))
                .concatMap(this::processTick)
                // takeLast(1).next() behaves like last() but emits empty instead of no such element error
                .takeLast(1).next()
                .flatMap(this::updateLatestSyncedTick); // skipped if error or empty
    }

    private Mono<Long> processTick(Long tickNumber) {
        return tickRepository.isProcessedTick(tickNumber)
                .flatMap(alreadyProcessed -> alreadyProcessed
                        ? Mono.just(tickNumber).doOnNext(n -> log.debug("Skipping already stored tick [{}].", n))
                        : processNewTick(tickNumber));
    }

    private Mono<Long> processNewTick(Long tickNumber) {
        return Mono.just(tickNumber); // FIXME
    }

    private Flux<Long> calculateSyncRange(Tuple2<Long, Long> startAndEndTick) {
        long startTick = startAndEndTick.getT1();
        long endTick = startAndEndTick.getT2(); // we could do +1 here because end tick is exclusive but we better wait one tick
        int numberOfTicks = (int) (endTick - startTick); // we don't sync the latest tick (integration api might still be behind)
        if (numberOfTicks > 0) {
            if (numberOfTicks > 1) {
                if (numberOfTicks > 5) {
                    log.info("Syncing range from tick [{}] (incl) to [{}] (excl). Number of ticks: [{}].", startTick, endTick, numberOfTicks);
                }
                return Flux.range(0, numberOfTicks).map(counter -> startTick + counter);
            } else {
                return Flux.just(startTick);
            }
        } else {
            log.debug("Nothing to sync... start [{}], end [{}]", startTick, endTick);
            return Flux.empty();
        }
    }

    private Mono<Tuple2<Long, Long>> calculateStartAndEndTick(Tuple2<TickInfo, Long> tuple) {
        return tickRepository.getLatestSyncedTick()
                .map(latestStoredTick -> latestStoredTick < tuple.getT1().initialTick()
                        ? tuple.getT1().initialTick()
                        : latestStoredTick + 1)
                // take the lowest common tick where event data is available (most probably always getT2())
                .map(startTick -> Tuples.of(startTick, Math.min(tuple.getT1().tick(), tuple.getT2())));
    }

    private Mono<Tuple2<TickInfo, Long>> getLatestAvailableTick() {
        return Mono.zip(coreService.getTickInfo(), eventService.getLatestTick())
                .doOnNext(tuple -> {
                    // log if there is a 'larger' gap between current tick and event service
                    if (Math.abs(tuple.getT1().tick() - tuple.getT2()) > 5) {
                        log.info("Current tick: [{}]. Events are available until tick [{}].",
                                tuple.getT1().tick(), tuple.getT2());
                    }
                });
    }

    private Mono<Long> updateLatestSyncedTick(long syncedTick) {
        // only update if it increased
        return tickRepository.getLatestSyncedTick()
                .flatMap(latest -> latest >= syncedTick
                        ? Mono.just(false)
                        : tickRepository.setLatestSyncedTick(syncedTick))
                .then(Mono.just(syncedTick));
    }

}
