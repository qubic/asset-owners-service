package org.qubic.aos.api.scheduler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.qubic.aos.api.redis.repository.QueueProcessingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class QueueProcessor<T> {

    protected final QueueProcessingRepository<T> redisRepository;

    public QueueProcessor(QueueProcessingRepository<T> redisRepository) {
        this.redisRepository = redisRepository;
    }

    public List<T> processQueue() {
        ArrayList<T> processed = new ArrayList<>();

        boolean itemsAvailable = true;
        do  {
            T sourceDto = redisRepository.readFromQueue();
            if (sourceDto == null) {
                itemsAvailable = false;
                log.debug("Queue is empty");
            } else {
                log.info("Processing from queue: {}", sourceDto);
                handle(sourceDto).ifPresent(processed::add);
            }

        } while (itemsAvailable);
        return processed;
    }

    private Optional<T> handle(@NonNull T item) {
        try {
            processQueueItem(item);
            removeFromProcessingQueue(item);
            return Optional.of(item);
        } catch (RuntimeException e) {
            log.error("Error processing redis message {}.", item, e);
            Long length = redisRepository.pushIntoErrorsQueue(item);
            log.warn("Moved message into error queue. Error queue length: [{}].", length);
            removeFromProcessingQueue(item);
            return Optional.empty();
        }
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    protected abstract void processQueueItem(final T item);

    protected void removeFromProcessingQueue(T sourceDto) {
        Long removed = redisRepository.removeFromProcessingQueue(sourceDto);
        log.info("Removed [{}] messages from processing queue.", removed);
    }

}
