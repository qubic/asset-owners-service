package org.qubic.as.sync.adapter;

import org.qubic.as.sync.domain.TickInfo;
import reactor.core.publisher.Mono;

public interface CoreApiService {

    Mono<TickInfo> getTickInfo();

}
