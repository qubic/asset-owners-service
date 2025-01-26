package org.qubic.aos.api.config;

import org.qubic.aos.api.db.AssetOwnersRepository;
import org.qubic.aos.api.db.AssetsDbService;
import org.qubic.aos.api.db.EntitiesDbService;
import org.qubic.aos.api.owners.TransferAssetsService;
import org.qubic.aos.api.owners.UniverseCsvImporter;
import org.qubic.aos.api.redis.AssetsCacheManager;
import org.qubic.aos.api.redis.repository.AssetChangeMessageReader;
import org.qubic.aos.api.redis.repository.AssetIssuanceMessageReader;
import org.qubic.aos.api.scheduler.AssetChangeProcessor;
import org.qubic.aos.api.scheduler.AssetIssuanceProcessor;
import org.qubic.aos.api.scheduler.RedisSyncScheduler;
import org.qubic.aos.api.scheduler.UniverseImportScheduler;
import org.qubic.aos.api.validation.ValidationUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@Configuration
public class SchedulerConfiguration {

    @Bean
    UniverseCsvImporter universeCsvImporter(ValidationUtility validationUtility, EntitiesDbService entitiesDbService,
                                            AssetsDbService assetsDbService, AssetOwnersRepository assetOwnersRepository,
                                            AssetsCacheManager assetsCacheManager) {
        return new UniverseCsvImporter(validationUtility, entitiesDbService, assetsDbService, assetOwnersRepository, assetsCacheManager);
    }

    @Bean
    UniverseImportScheduler universeImportScheduler(UniverseCsvImporter universeCsvImporter)
            throws IOException {
        return new UniverseImportScheduler(universeCsvImporter);
    }

    @Bean
    TransferAssetsService transferAssetsService(AssetsDbService assetsDbService, EntitiesDbService entitiesDbService,
                                                AssetOwnersRepository assetOwnersRepository, ValidationUtility validationUtility) {
        return new TransferAssetsService(assetsDbService, entitiesDbService, assetOwnersRepository, validationUtility);
    }

    @Bean
    AssetChangeProcessor assetChangeProcessor(AssetChangeMessageReader assetChangeMessageReader,
                                              TransferAssetsService transferAssetsService,
                                              AssetsCacheManager assetsCacheManager) {
        return new AssetChangeProcessor(assetChangeMessageReader, transferAssetsService, assetsCacheManager);
    }

    @Bean
    AssetIssuanceProcessor assetIssuanceProcessor(AssetIssuanceMessageReader assetIssuanceMessageReader, TransferAssetsService transferAssetsService) {
        return new AssetIssuanceProcessor(assetIssuanceMessageReader, transferAssetsService);
    }

    @Bean
    RedisSyncScheduler redisSyncScheduler(AssetIssuanceProcessor assetIssuanceProcessor, AssetChangeProcessor assetChangeProcessor) {
        return new RedisSyncScheduler(assetIssuanceProcessor, assetChangeProcessor);
    }

}
