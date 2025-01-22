package org.qubic.as.sync.adapter.il;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.adapter.EventApiService;
import org.qubic.as.sync.adapter.exception.EmptyResultException;
import org.qubic.as.sync.adapter.il.domain.IlEventStatusResponse;
import org.qubic.as.sync.adapter.il.domain.IlTickEvents;
import org.qubic.as.sync.domain.EpochAndTick;
import org.qubic.as.sync.domain.TransactionEvents;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class IntegrationEventApiService implements EventApiService {

    private static final int NUM_RETRIES = 3;
    private static final String BASE_PATH = "/v1/events";
    private final WebClient webClient;

    public IntegrationEventApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<List<TransactionEvents>> getTickEvents(long tick) {
        return webClient.post()
                .uri(BASE_PATH + "/getTickEvents")
                .bodyValue(tickPayloadBody(tick))
                .retrieve()
                .bodyToMono(IlTickEvents.class)
                .map(IlTickEvents::txEvents)
                .switchIfEmpty(Mono.error(emptyGetEventsResult(tick)))
                .doOnError(e -> log.error("Error getting tick events: {}", e.getMessage()))
                .retryWhen(retrySpec());
    }

    @Override
    public Mono<EpochAndTick> getLastProcessedTick() {
        return webClient.get()
                .uri(BASE_PATH + "/status")
                .retrieve()
                .bodyToMono(IlEventStatusResponse.class)
                .map(IlEventStatusResponse::lastProcessedTick)
                .switchIfEmpty(Mono.error(new EmptyResultException("Could not get event status.")))
                .doOnError(e -> log.error("Error getting last processed tick: {}", e.getMessage()))
                .retryWhen(retrySpec());
    }

    private static RetryBackoffSpec retrySpec() {
        return Retry.backoff(NUM_RETRIES, Duration.ofSeconds(1)).doBeforeRetry(c -> log.info("Retry: [{}].", c.totalRetries() + 1));
    }

    private static String tickPayloadBody(long tick) {
        return String.format("{\"tick\":%d}", tick);
    }

    private static EmptyResultException emptyGetEventsResult(long tick) {
        return new EmptyResultException(String.format("Could not get events for tick [%d].", tick));
    }

}
