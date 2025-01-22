package org.qubic.as.sync.adapter.il;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.adapter.exception.EmptyResultException;
import org.qubic.as.sync.adapter.il.domain.IlTickInfo;
import org.qubic.as.sync.adapter.il.mapping.IlCoreMapper;
import org.qubic.as.sync.domain.TickInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Slf4j
public class IntegrationCoreApiService implements CoreApiService {

    private static final String CORE_BASE_PATH_V1 = "/v1/core";
    private static final int NUM_RETRIES = 3;
    private final WebClient webClient;
    private final IlCoreMapper mapper;

    public IntegrationCoreApiService(WebClient webClient, IlCoreMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    @Override
    public Mono<TickInfo> getTickInfo() {
        return webClient.get()
                .uri(CORE_BASE_PATH_V1 + "/getTickInfo")
                .retrieve()
                .bodyToMono(IlTickInfo.class)
                .map(mapper::map)
                .switchIfEmpty(Mono.error(new EmptyResultException("Could not get tick info.")))
                .doOnError(e -> log.error("Error getting tick info: {}", e.getMessage()))
                .retryWhen(retrySpec());
    }

    private static RetryBackoffSpec retrySpec() {
        return Retry.backoff(NUM_RETRIES, Duration.ofSeconds(1)).doBeforeRetry(c -> log.info("Retry: [{}].", c.totalRetries() + 1));
    }

}
