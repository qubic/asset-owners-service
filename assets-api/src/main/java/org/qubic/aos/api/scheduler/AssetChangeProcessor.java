package org.qubic.aos.api.scheduler;

import at.qubic.api.domain.event.EventType;
import org.qubic.aos.api.owners.TransferAssetsService;
import org.qubic.aos.api.redis.AssetsCacheManager;
import org.qubic.aos.api.redis.repository.QueueProcessingRepository;
import org.qubic.as.messages.AssetChangeMessage;

public class AssetChangeProcessor extends QueueProcessor<AssetChangeMessage> {

    private final TransferAssetsService transferAssetsService;
    private final AssetsCacheManager assetsCacheManager;

    public AssetChangeProcessor(QueueProcessingRepository<AssetChangeMessage> redisRepository,
                                TransferAssetsService transferAssetsService,
                                AssetsCacheManager assetsCacheManager) {
        super(redisRepository);
        this.transferAssetsService = transferAssetsService;
        this.assetsCacheManager = assetsCacheManager;
    }

    @Override
    protected void processQueueItem(AssetChangeMessage item) {
        if (item.eventType() == EventType.ASSET_OWNERSHIP_CHANGE.getCode()) {
            transferAssetsService.transfer(item.source(), item.destination(), item.issuer(), item.assetName(), item.numberOfShares());
            assetsCacheManager.clearAssetOwnersCache(item.issuer(), item.assetName());
        } else {
            throw new IllegalArgumentException("Invalid event type: " + item.eventType());
        }

    }

}
