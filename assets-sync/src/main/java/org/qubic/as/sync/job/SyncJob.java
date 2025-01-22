package org.qubic.as.sync.job;

import reactor.core.publisher.Mono;

public class SyncJob {

    public Mono<Long> sync() {

        return Mono.just(666L); // FIXME

    }

}
