package org.qubic.aos.api.scheduler;

import org.qubic.aos.api.owners.TransferAssetsService;
import org.qubic.aos.api.redis.repository.QueueProcessingRepository;
import org.qubic.as.messages.AssetChangeMessage;

public class AssetChangeProcessor extends QueueProcessor<AssetChangeMessage> {

    private final TransferAssetsService transferAssetsService;

    public AssetChangeProcessor(QueueProcessingRepository<AssetChangeMessage> redisRepository,
                                TransferAssetsService transferAssetsService) {
        super(redisRepository);
        this.transferAssetsService = transferAssetsService;
    }

    @Override
    protected void processQueueItem(AssetChangeMessage item) {
        transferAssetsService.transfer(item.source(), item.destination(), item.issuer(), item.assetName(), item.numberOfShares());
    }

}
