package org.qubic.as.sync.adapter;

import org.qubic.as.sync.domain.TransactionEvents;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventApiService {

    Mono<List<TransactionEvents>> getTickEvents(long tick);
    Mono<Long> getLastProcessedTick();

}
