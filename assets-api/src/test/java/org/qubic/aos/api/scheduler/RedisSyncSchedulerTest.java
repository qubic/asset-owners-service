package org.qubic.aos.api.scheduler;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RedisSyncSchedulerTest {

    private final AssetIssuanceProcessor assetIssuanceProcessor = mock();
    private final AssetChangeProcessor assetChangeProcessor = mock();

    private final RedisSyncScheduler redisSyncScheduler = new RedisSyncScheduler(assetIssuanceProcessor, assetChangeProcessor);

    @Test
    void processSyncQueues_thenCallAssetIssuanceProcessor() {
        redisSyncScheduler.processSyncQueues();
        verify(assetIssuanceProcessor).processQueue();
    }

    @Test
    void processSyncQueues_thenCallAssetChangeProcessor() {
        redisSyncScheduler.processSyncQueues();
        verify(assetChangeProcessor).processQueue();
    }

}