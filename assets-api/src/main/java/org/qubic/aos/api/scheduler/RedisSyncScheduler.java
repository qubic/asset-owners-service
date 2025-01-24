package org.qubic.aos.api.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
public class RedisSyncScheduler {

    private final AssetIssuanceProcessor assetIssuanceProcessor;
    private final AssetChangeProcessor assetChangeProcessor;

    public RedisSyncScheduler(AssetIssuanceProcessor assetIssuanceProcessor, AssetChangeProcessor assetChangeProcessor) {
        this.assetIssuanceProcessor = assetIssuanceProcessor;
        this.assetChangeProcessor = assetChangeProcessor;
    }

    @Scheduled(cron = "${scheduler.sync.cron}")
    void processTradesAndTransactions() {
        log.debug("Running data synchronization...");

        // TODO test
        List<AssetIssuanceMessage> issuanceMessages = assetIssuanceProcessor.processQueue();
        if (CollectionUtils.size(issuanceMessages) > 0) {
            log.info("Processed [{}] asset issuance messages", issuanceMessages.size());
        }

        // TODO test
        List<AssetChangeMessage> assetChangeMessages = assetChangeProcessor.processQueue();
        if (CollectionUtils.size(issuanceMessages) > 0) {
            log.info("Processed [{}] asset change messages.", assetChangeMessages.size());
        }

    }

}
