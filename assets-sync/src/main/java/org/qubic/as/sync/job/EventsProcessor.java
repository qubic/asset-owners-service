package org.qubic.as.sync.job;

import at.qubic.api.domain.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.domain.AssetChangeEvent;
import org.qubic.as.sync.domain.AssetEvents;
import org.qubic.as.sync.repository.AssetChangeMessageQueue;
import org.qubic.as.sync.repository.AssetIssuanceMessageQueue;
import org.qubic.as.sync.repository.mapper.AssetChangeMessageMapper;
import org.qubic.as.sync.repository.mapper.AssetIssuanceMessageMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class EventsProcessor {

    private final AssetIssuanceMessageQueue issuanceMessageQueue;
    private final AssetIssuanceMessageMapper issuanceMessageMapper;

    private final AssetChangeMessageQueue changeMessageQueue;
    private final AssetChangeMessageMapper changeMessageMapper;

    public EventsProcessor(AssetIssuanceMessageQueue issuanceMessageQueue, AssetIssuanceMessageMapper issuanceMessageMapper, AssetChangeMessageQueue changeMessageQueue, AssetChangeMessageMapper changeMessageMapper) {
        this.issuanceMessageQueue = issuanceMessageQueue;
        this.issuanceMessageMapper = issuanceMessageMapper;
        this.changeMessageQueue = changeMessageQueue;
        this.changeMessageMapper = changeMessageMapper;
    }

    public Mono<Long> process(Long tickNumber, AssetEvents assetEvents) {
        Flux<Long> processAssetIssuanceEvents = Flux.fromIterable(assetEvents. issuanceEvents())
                .doOnNext(EventsProcessor::logEvent)
                .map(issuanceMessageMapper::map)
                .doOnNext(EventsProcessor::logMessage)
                .flatMap(issuanceMessageQueue::send);

        Flux<Long> processAssetChangeEvents = Flux.fromIterable(assetEvents.transferEvents())
                .doOnNext(EventsProcessor::logEvent)
                .filter(EventsProcessor::isAssetOwnershipChange)
                .map(changeMessageMapper::map)
                .doOnNext(EventsProcessor::logMessage)
                .flatMap(changeMessageQueue::send);

        return processAssetIssuanceEvents
                .thenMany(processAssetChangeEvents)
                .then(Mono.just(tickNumber));
    }

    private static void logEvent(Object event) {
        log.debug("Event: {}", event);
    }

    private static void logMessage(Object message) {
        log.info("Sending: {}", message);
    }

    private static boolean isAssetOwnershipChange(AssetChangeEvent ae) {
        return ae.eventType() == EventType.ASSET_OWNERSHIP_CHANGE.getCode();
    }

}
