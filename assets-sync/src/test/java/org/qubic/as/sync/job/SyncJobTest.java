package org.qubic.as.sync.job;

import org.junit.jupiter.api.Test;
import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.adapter.EventApiService;
import org.qubic.as.sync.adapter.exception.EmptyResultException;
import org.qubic.as.sync.domain.EpochAndTick;
import org.qubic.as.sync.domain.TickInfo;
import org.qubic.as.sync.repository.TickRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class SyncJobTest {

    private final CoreApiService coreService = mock();
    private final EventApiService eventService = mock();
    private final TickRepository  tickRepository = mock();
    private final SyncJob syncJob = new SyncJob(coreService, eventService, tickRepository);

    @Test
    void sync_givenNoNewTick_thenDoNotSync() {
        TickInfo currentTickInfo = new TickInfo(1, 3459, 3456);
        when(coreService.getTickInfo()).thenReturn(Mono.just(currentTickInfo));
        when(eventService.getLastProcessedTick()).thenReturn(Mono.just(new EpochAndTick(1, 3459)));
        when(tickRepository.getLatestSyncedTick()).thenReturn(Mono.just(3459L));

        StepVerifier.create(syncJob.sync())
                .verifyComplete();

        verify(coreService).getTickInfo();
        verify(tickRepository).getLatestSyncedTick();
        verify(eventService).getLastProcessedTick();
        verifyNoMoreInteractions(tickRepository, coreService, eventService);
    }

    @Test
    void sync_givenOneNewTick_thenProcessTick() {
        TickInfo currentTickInfo = new TickInfo(1, 3460, 0);
        EpochAndTick latestEventTick = new EpochAndTick(1, 3460);

        when(coreService.getTickInfo()).thenReturn(Mono.just(currentTickInfo));
        when(eventService.getLastProcessedTick()).thenReturn(Mono.just(latestEventTick));
        when(tickRepository.getLatestSyncedTick()).thenReturn(Mono.just(3458L));

        when(tickRepository.isProcessedTick(anyLong())).thenReturn(Mono.just(false));
        when(tickRepository.addToProcessedTicks(anyLong())).thenReturn(Mono.just(true));
        when(tickRepository.setLatestSyncedTick(3459L)).thenReturn(Mono.just(true));

        StepVerifier.create(syncJob.sync().log())
                .expectNext(3459L)
                .verifyComplete();
    }

    @Test
    void sync_givenTickRange_thenProcessTicks() {
        TickInfo currentTickInfo = new TickInfo(1, 3460, 0);
        EpochAndTick latestEventTick = new EpochAndTick(1, 3460);

        when(coreService.getTickInfo()).thenReturn(Mono.just(currentTickInfo));
        when(eventService.getLastProcessedTick()).thenReturn(Mono.just(latestEventTick));
        when(tickRepository.getLatestSyncedTick()).thenReturn(Mono.just(3455L));

        when(tickRepository.isProcessedTick(anyLong())).thenReturn(Mono.just(false));
        when(tickRepository.addToProcessedTicks(anyLong())).thenReturn(Mono.just(true));
        when(tickRepository.setLatestSyncedTick(3459)).thenReturn(Mono.just(true));

        StepVerifier.create(syncJob.sync().log())
                .expectNext(3459L)
                .verifyComplete();

        verify(tickRepository).isProcessedTick(3456);
        verify(tickRepository).isProcessedTick(3457);
        verify(tickRepository).isProcessedTick(3458);
        verify(tickRepository).isProcessedTick(3459);
        verify(tickRepository).isProcessedTick(3459);
    }

    @Test
    void sync_givenEventsNotAvailableYet_thenSyncUntilLatestAvailableTick() {
        TickInfo currentTickInfo = new TickInfo(1, 3461, 0); // latest tick 3461
        EpochAndTick latestEventTick = new EpochAndTick(1, 3460); // events only available until 3460

        when(coreService.getTickInfo()).thenReturn(Mono.just(currentTickInfo));
        when(eventService.getLastProcessedTick()).thenReturn(Mono.just(latestEventTick));
        when(tickRepository.getLatestSyncedTick()).thenReturn(Mono.just(3458L));

        when(tickRepository.setLatestSyncedTick(anyLong())).thenReturn(Mono.just(true));
        when(tickRepository.isProcessedTick(anyLong())).thenReturn(Mono.just(false));
        when(tickRepository.addToProcessedTicks(3459)).thenReturn(Mono.just(true));

        StepVerifier.create(syncJob.sync().log())
                .expectNext(3459L)
                .verifyComplete();
    }

    @Test
    void sync_givenError_thenAbortRun() {
        when(coreService.getTickInfo()).thenReturn(Mono.error(new EmptyResultException("test")));
        when(eventService.getLastProcessedTick()).thenReturn(Mono.just(new EpochAndTick(1, 3460)));

        StepVerifier.create(syncJob.sync().log())
                .expectError(EmptyResultException.class)
                .verify();

        verifyNoInteractions(tickRepository);
    }

}