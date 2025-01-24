package org.qubic.aos.api.scheduler;

import org.qubic.aos.api.owners.TransferAssetsService;
import org.qubic.aos.api.redis.repository.QueueProcessingRepository;
import org.qubic.as.messages.AssetIssuanceMessage;

public class AssetIssuanceProcessor extends QueueProcessor<AssetIssuanceMessage> {

    private final TransferAssetsService transferAssetsService;

    public AssetIssuanceProcessor(QueueProcessingRepository<AssetIssuanceMessage> redisRepository,
                                  TransferAssetsService transferAssetsService) {
        super(redisRepository);
        this.transferAssetsService = transferAssetsService;
    }

    @Override
    protected void processQueueItem(AssetIssuanceMessage item) {
        transferAssetsService.issueAsset(item.issuer(), item.assetName(), item.numberOfShares());
    }

}
