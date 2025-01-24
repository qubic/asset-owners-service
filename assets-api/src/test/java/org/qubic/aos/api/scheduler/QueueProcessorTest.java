package org.qubic.aos.api.scheduler;

import org.junit.jupiter.api.Test;
import org.qubic.aos.api.redis.repository.QueueProcessingRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class QueueProcessorTest<S> {

    protected final QueueProcessingRepository<S> redisRepository;
    protected final QueueProcessor<S> processor;

    public QueueProcessorTest() {
        this.redisRepository = mock();
        processor = new QueueProcessor<>(redisRepository) {
            @Override
            protected void processQueueItem(S item) {
                // empty
            }
        };
    }

    @Test
    void process_thenSaveAndReturnDto() {
        S dto = createSourceMock();

        when(redisRepository.readFromQueue()).thenReturn(dto, (S) null);

        List<S> targetDtos = processor.processQueue();
        assertThat(targetDtos).contains(dto);

        verify(redisRepository).removeFromProcessingQueue(dto);
        verify(redisRepository, never()).pushIntoErrorsQueue(any());
    }

    @Test
    void process_givenError_thenMoveIntoErrorQueue() {
        QueueProcessor<S> processor = new QueueProcessor<>(redisRepository) {
            @Override
            protected void processQueueItem(S item) {
                throw new RuntimeException("test");
            }
        };
        S dto = createSourceMock();

        when(redisRepository.readFromQueue()).thenReturn(dto, (S) null);

        List<S> targetDtos = processor.processQueue();
        assertThat(targetDtos).isEmpty();

        verify(redisRepository).pushIntoErrorsQueue(dto);
        verify(redisRepository).removeFromProcessingQueue(dto);
    }

    // needed to create source and targets with correct type
    protected S createSourceMock() { return mock(); }

}