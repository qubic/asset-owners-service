package org.qubic.aos.api.scheduler;

import at.qubic.api.domain.event.EventType;
import org.junit.jupiter.api.Test;
import org.qubic.aos.api.owners.TransferAssetsService;
import org.qubic.aos.api.redis.AssetsCacheManager;
import org.qubic.aos.api.redis.repository.QueueProcessingRepository;
import org.qubic.as.messages.AssetChangeMessage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetChangeProcessorTest {

    private final TransferAssetsService transferAssetsService = mock();
    private final AssetsCacheManager assetsCacheManager = mock();
    private final QueueProcessingRepository<AssetChangeMessage> redisRepository = mock();

    private final AssetChangeProcessor processor = new AssetChangeProcessor(redisRepository, transferAssetsService, assetsCacheManager);

    @Test
    void process_thenTransferAsset() {
        AssetChangeMessage message = new AssetChangeMessage("source", "destination", "issuer",
                "assetName", 1, "transactionHash", 42, EventType.ASSET_OWNERSHIP_CHANGE.getCode());
        processor.processQueueItem(message);
        verify(transferAssetsService).transfer("source", "destination", "issuer", "assetName", 1);
    }

    @Test
    void process_thenClearCaches() {
        AssetChangeMessage message = new AssetChangeMessage("source", "destination", "issuer",
                "assetName", 1, "transactionHash", 42, EventType.ASSET_OWNERSHIP_CHANGE.getCode());
        processor.processQueueItem(message);
        verify(assetsCacheManager).clearAssetOwnersCache("issuer", "assetName");
    }

    @Test
    void process_givenInvalidType_thenThrow() {
        AssetChangeMessage message = new AssetChangeMessage("source", "destination", "issuer",
                "assetName", 1, "transactionHash", 42, EventType.ASSET_POSSESSION_CHANGE.getCode());
        assertThatThrownBy(() -> processor.processQueueItem(message)).isInstanceOf(IllegalArgumentException.class);
    }

}