package org.qubic.as.sync.job;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.qubic.as.sync.properties.SyncJobProperties;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.*;

@Slf4j
class SyncJobRunnerTest {

    private final SyncJobProperties syncJobProperties = mock();
    private final SyncJob syncJob = mock();
    private final SyncJobRunner syncJobRunner = new SyncJobRunner(syncJob, syncJobProperties);

    @Test
    void loop_givenDisabled_thenDoNotRunJob() {
        syncJobRunner.loop().blockLast();
        verifyNoInteractions(syncJob);
    }

    @Test
    void loop_givenEnabled_thenRunJob() {
        when(syncJobProperties.isEnabled()).thenReturn(true);
        when(syncJobProperties.getRepeats()).thenReturn(0L); // repeat once
        when(syncJobProperties.getSleepInterval()).thenReturn(Duration.ofMillis(100)); // do not repeat
        when(syncJob.sync()).thenReturn(Mono.just(666L));

        StepVerifier.create(syncJobRunner.loop())
                .expectNext(666L)
                .expectComplete()
                .verify(Duration.ofSeconds(1));

        verify(syncJob, times(1)).sync();
    }

    @Test
    void loop_givenRepeatEnabled_thenRepeat() {
        final Duration sleepDuration = Duration.ofSeconds(1);
        when(syncJobProperties.isEnabled()).thenReturn(true);
        when(syncJobProperties.getRepeats()).thenReturn(1L); // repeat once
        when(syncJobProperties.getSleepInterval()).thenReturn(sleepDuration); // do not repeat
        when(syncJob.sync()).thenReturn(Mono.just(666L).doOnNext(x -> log.info("run sync now")));

        // syncJobRunner.loop().blockLast();
        StepVerifier.withVirtualTime(syncJobRunner::loop)
                .expectSubscription()
                .expectNoEvent(sleepDuration)
                .expectNext(666L)
                .expectNoEvent(sleepDuration)
                .expectNext(666L)
                .expectComplete()
                .verify(Duration.ofMillis(100));

        verify(syncJob, times(2)).sync();
    }

    @Test
    void loop_givenError_thenRunRetry() {

        final Duration sleepDuration = Duration.ofSeconds(1);
        final Duration retryInterval = Duration.ofSeconds(3);

        when(syncJobProperties.isEnabled()).thenReturn(true);
        when(syncJobProperties.getSleepInterval()).thenReturn(sleepDuration);
        when(syncJobProperties.getRetryInterval()).thenReturn(retryInterval);

        when(syncJob.sync())
                .thenReturn(Mono.error(new RuntimeException("test")))
                .thenReturn(Mono.just(666L).doOnNext(x -> log.info("run sync successfully")));

        // syncJobRunner.loop().blockLast();
        StepVerifier.withVirtualTime(syncJobRunner::loop)
                .expectSubscription()
                .expectNoEvent(sleepDuration.plus(retryInterval).plus(sleepDuration))
                .expectNext(666L)
                .expectComplete()
                .verify(Duration.ofMillis(500));

        verify(syncJob, times(2)).sync();
    }

}