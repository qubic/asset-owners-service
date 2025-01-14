package org.qubic.aos.api.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class RedisSyncScheduler {

    @Scheduled(cron = "${scheduler.sync.cron}")
    void processTradesAndTransactions() {
        log.debug("Running data synchronization...");
        // TODO implement
    }

}
