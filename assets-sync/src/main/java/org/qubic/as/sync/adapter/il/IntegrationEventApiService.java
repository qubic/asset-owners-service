package org.qubic.as.sync.adapter.il;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.adapter.EventApiService;
import org.qubic.as.sync.adapter.exception.EmptyResultException;
import org.qubic.as.sync.adapter.il.domain.IlAssetEvents;
import org.qubic.as.sync.adapter.il.mapping.IlEventMapper;
import org.qubic.as.sync.domain.AssetEvents;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Slf4j
public class IntegrationEventApiService implements EventApiService {

    private final int retries;
    private final WebClient webClient;
    private final IlEventMapper mapper;

    public IntegrationEventApiService(WebClient webClient, IlEventMapper mapper, int retries) {
        this.webClient = webClient;
        this.mapper = mapper;
        log.info("Number of retries: [{}]", retries);
        this.retries = retries;
    }

    @Override
    public Mono<AssetEvents> getTickEvents(long tick) {
        return webClient.get()
                .uri("/v1/ticks/{tick}/events/assets", tick)
                .retrieve()
                .bodyToMono(IlAssetEvents.class)
                .map(mapper::map)
                .switchIfEmpty(Mono.error(emptyGetEventsResult(tick)))
                .doOnError(e -> log.error("Error getting tick events: {}", e.getMessage()))
                .retryWhen(retrySpec());
    }

    @Override
    public Mono<Long> getLatestTick() {
        return webClient.get()
                .uri("/v1/ticks/0/events/assets") // call dummy tick number 0
                .retrieve()
                .bodyToMono(IlAssetEvents.class)
                .map(IlAssetEvents::latestTick)
                .switchIfEmpty(Mono.error(new EmptyResultException("Empty result getting latest event tick.")))
                .doOnError(e -> log.error("Error getting last processed tick: {}", e.getMessage()))
                .retryWhen(retrySpec());
    }

    private RetryBackoffSpec retrySpec() {
        return Retry.backoff(retries, Duration.ofSeconds(1)).doBeforeRetry(c -> log.info("Retry: [{}].", c.totalRetries() + 1));
    }

    private static EmptyResultException emptyGetEventsResult(long tick) {
        return new EmptyResultException(String.format("Could not get events for tick [%d].", tick));
    }

}
