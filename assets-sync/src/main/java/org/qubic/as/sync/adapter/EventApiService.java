package org.qubic.as.sync.adapter;

import org.qubic.as.sync.domain.AssetEvents;
import reactor.core.publisher.Mono;

public interface EventApiService {

    Mono<AssetEvents> getTickEvents(long tick);
    Mono<Long> getLatestTick();

}
