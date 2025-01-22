package org.qubic.as.sync.job;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.properties.SyncJobProperties;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.function.Consumer;

@Slf4j
public class SyncJobRunner {

    private final SyncJob syncJob;
    private final SyncJobProperties properties;

    public SyncJobRunner(SyncJob syncJob, SyncJobProperties properties) {
        this.syncJob = syncJob;
        this.properties = properties;
    }

    public Flux<Long> loop() {
        if (properties.isEnabled()) {
            log.info("Sync job is enabled.");
            return Mono.defer(() -> syncJob.sync() // defer for re-subscription
                            // we sleep before every run. That's easier than sleeping after repeat.
                            .delaySubscription(properties.getSleepInterval()))
                    .doOnNext(tick -> log.debug("Sync to [{}] completed.", tick))
                    .doOnError(logError())
                    .retryWhen(getRetrySpec(properties.getRetryInterval()))
                    .doOnTerminate(() -> log.debug("Sync run finished. Next run in [{}].", properties.getSleepInterval()))
                    .repeat(properties.getRepeats() < 0 ? Long.MAX_VALUE : properties.getRepeats())
                    .doOnNext(x -> log.debug("Repeat..."));
        } else {
            log.info("Sync job is disabled.");
            return Flux.empty();
        }
    }
    private static Consumer<Throwable> logError() {
        return t -> {
            if (reactor.core.Exceptions.isRetryExhausted(t) && t.getCause() instanceof WebClientResponseException wce) {
                log.error("Error running sync job: {}. Cause: {}", t.getMessage(), wce.getMessage());
            } else {
                log.error("Error running sync job.", t);
            }
        };
    }

    private static RetryBackoffSpec getRetrySpec(Duration retryDuration) {
        // important: do not backoff on retry. this will increase back off interval too much in case there are
        // frequent failures like rate limiting.
        return Retry.fixedDelay(Long.MAX_VALUE, retryDuration)
                .doBeforeRetry(retrySignal -> log.info("Retrying sync job. Subsequent retries [{}], total retries [{}].",
                        retrySignal.totalRetriesInARow(), retrySignal.totalRetries()));
    }

}
