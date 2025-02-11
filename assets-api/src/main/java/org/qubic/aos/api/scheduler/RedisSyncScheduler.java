package org.qubic.aos.api.scheduler;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RedisSyncScheduler {

    private final AssetIssuanceProcessor assetIssuanceProcessor;
    private final AssetChangeProcessor assetChangeProcessor;

    // export latest processed tick as metric
    private final AtomicLong latestMessageTick = Objects.requireNonNull(Metrics.gauge("tick.latest", Tags.of("source", "message"), new AtomicLong(0)));

    public RedisSyncScheduler(AssetIssuanceProcessor assetIssuanceProcessor, AssetChangeProcessor assetChangeProcessor) {
        this.assetIssuanceProcessor = assetIssuanceProcessor;
        this.assetChangeProcessor = assetChangeProcessor;
    }

    @Scheduled(cron = "${scheduler.sync.cron}")
    void processSyncQueues() {
        log.debug("Running data synchronization...");

        List<AssetIssuanceMessage> issuanceMessages = assetIssuanceProcessor.processQueue();
        if (CollectionUtils.size(issuanceMessages) > 0) {
            log.info("Processed [{}] asset issuance messages", issuanceMessages.size());
            issuanceMessages.forEach(message -> updateProcessedTick(message.tickNumber()));
        }

        List<AssetChangeMessage> assetChangeMessages = assetChangeProcessor.processQueue();
        if (CollectionUtils.size(assetChangeMessages) > 0) {
            log.info("Processed [{}] asset change messages.", assetChangeMessages.size());
            assetChangeMessages.forEach(message -> updateProcessedTick(message.tickNumber()));
        }

    }

    private void updateProcessedTick(long tick) {
        if (latestMessageTick.get() < tick) {
            latestMessageTick.lazySet(tick);
        }
    }

}
