package org.qubic.aos.api.scheduler;

import org.junit.jupiter.api.Test;
import org.qubic.aos.api.owners.TransferAssetsService;
import org.qubic.aos.api.redis.repository.QueueProcessingRepository;
import org.qubic.as.messages.AssetIssuanceMessage;

import static org.mockito.Mockito.*;

class AssetIssuanceProcessorTest {

    private final TransferAssetsService transferAssetsService = mock();
    private final QueueProcessingRepository<AssetIssuanceMessage> redisRepository = mock();

    private final AssetIssuanceProcessor processor = new AssetIssuanceProcessor(redisRepository, transferAssetsService);

    @Test
    void process() {
        AssetIssuanceMessage message = new AssetIssuanceMessage("issuer", "name", 1, "hash", 2);
        processor.processQueueItem(message);
        verify(transferAssetsService).issueAsset("issuer", "name", 1);
    }

}