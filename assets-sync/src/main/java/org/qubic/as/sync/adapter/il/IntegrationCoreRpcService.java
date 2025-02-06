package org.qubic.as.sync.adapter.il;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.adapter.exception.EmptyResultException;
import org.qubic.as.sync.adapter.il.domain.IlRpcTickInfoResponse;
import org.qubic.as.sync.adapter.il.mapping.IlCoreMapper;
import org.qubic.as.sync.domain.TickInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Slf4j
public class IntegrationCoreRpcService implements CoreApiService {

    private final int retries;
    private final WebClient webClient;
    private final IlCoreMapper mapper;

    public IntegrationCoreRpcService(WebClient webClient, IlCoreMapper mapper, int retries) {
        this.webClient = webClient;
        this.mapper = mapper;
        log.info("Number of retries: [{}]", retries);
        this.retries = retries;
    }

    @Override
    public Mono<TickInfo> getTickInfo() {
        return webClient.get()
                .uri("/v1/tick-info")
                .retrieve()
                .bodyToMono(IlRpcTickInfoResponse.class)
                .map(IlRpcTickInfoResponse::tickInfo)
                .map(mapper::map)
                .switchIfEmpty(Mono.error(new EmptyResultException("Could not get tick info.")))
                .doOnError(e -> log.error("Error getting tick info: {}", e.getMessage()))
                .retryWhen(retrySpec());
    }

    private RetryBackoffSpec retrySpec() {
        return Retry.backoff(retries, Duration.ofSeconds(1)).doBeforeRetry(c -> log.info("Retry: [{}].", c.totalRetries() + 1));
    }

}
