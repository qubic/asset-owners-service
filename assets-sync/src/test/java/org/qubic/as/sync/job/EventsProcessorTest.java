package org.qubic.as.sync.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.qubic.as.sync.domain.AssetChangeEvent;
import org.qubic.as.sync.domain.AssetEvents;
import org.qubic.as.sync.domain.AssetIssuanceEvent;
import org.qubic.as.sync.repository.AssetChangeMessageQueue;
import org.qubic.as.sync.repository.AssetIssuanceMessageQueue;
import org.qubic.as.sync.repository.mapper.AssetChangeMessageMapper;
import org.qubic.as.sync.repository.mapper.AssetIssuanceMessageMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

class EventsProcessorTest {

    private final AssetIssuanceMessageQueue issuanceMessageQueue = mock();
    private final AssetIssuanceMessageMapper issuanceMessageMapper = mock();
    private final AssetChangeMessageQueue changeMessageQueue = mock();
    private final AssetChangeMessageMapper changeMessageMapper = mock();

    private final EventsProcessor processor = new EventsProcessor(issuanceMessageQueue, issuanceMessageMapper, changeMessageQueue , changeMessageMapper);


    @BeforeEach
    void initMocks() {
        when(issuanceMessageQueue.send(any(AssetIssuanceMessage.class))).thenReturn(Mono.just(42L));
        when(changeMessageQueue.send(any(AssetChangeMessage.class))).thenReturn(Mono.just(42L));
    }

    @Test
    void process_givenNoEvents_thenReturnTickNumber() {
        AssetEvents assetEvents = new AssetEvents(666, List.of(), List.of());

        StepVerifier.create(processor.process(12345L, assetEvents))
                .expectNext(12345L)
                .verifyComplete();

        verifyNoInteractions(issuanceMessageMapper);
        verifyNoInteractions(issuanceMessageQueue);
        verifyNoInteractions(changeMessageMapper);
        verifyNoInteractions(changeMessageQueue);
    }

    @Test
    void process_givenAssetIssuance_thenSendMessage() {
        AssetIssuanceEvent issuanceEvent = new AssetIssuanceEvent("issuer", "asset", 1, "hash", 2);
        AssetIssuanceMessage issuanceMessage = new AssetIssuanceMessage("issuer", "asset", 1, "hash", 2);
        when(issuanceMessageMapper.map(issuanceEvent)).thenReturn(issuanceMessage);
        AssetEvents assetEvents = new AssetEvents(666, List.of(), List.of(issuanceEvent));

        StepVerifier.create(processor.process(12345L, assetEvents))
                .expectNext(12345L)
                .verifyComplete();

        verify(issuanceMessageQueue).send(issuanceMessage);
        verifyNoInteractions(changeMessageMapper);
        verifyNoInteractions(changeMessageQueue);
    }

    @Test
    void process_givenAssetTransfer_thenSendMessage() {
        // possession change events is ignored
        AssetChangeEvent possessionChange = new AssetChangeEvent("source", "destination", "issuer", "asset", 1, "hash", 4, 3);
        AssetChangeEvent ownershipChange = new AssetChangeEvent("source", "destination", "issuer", "asset", 1, "hash", 4, 2);
        AssetChangeMessage changeMessage = new AssetChangeMessage("source", "destination", "issuer", "asset", 1, "hash", 4, 2);
        when(changeMessageMapper.map(ownershipChange)).thenReturn(changeMessage);
        AssetEvents assetEvents = new AssetEvents(666, List.of(possessionChange, ownershipChange), List.of());

        StepVerifier.create(processor.process(12345L, assetEvents))
                .expectNext(12345L)
                .verifyComplete();

        verify(changeMessageQueue).send(changeMessage);
        verifyNoMoreInteractions(changeMessageQueue);
        verifyNoInteractions(issuanceMessageMapper);
        verifyNoInteractions(issuanceMessageQueue);
    }

}