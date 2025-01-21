package org.qubic.aos.api.config;

import org.qubic.aos.api.db.AssetOwnersRepository;
import org.qubic.aos.api.db.AssetsDbService;
import org.qubic.aos.api.db.EntitiesDbService;
import org.qubic.aos.api.owners.UniverseCsvImporter;
import org.qubic.aos.api.scheduler.RedisSyncScheduler;
import org.qubic.aos.api.scheduler.UniverseImportScheduler;
import org.qubic.aos.api.validation.ValidationUtility;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@Configuration
public class SchedulerConfiguration {

    @Bean
    UniverseCsvImporter universeCsvImporter(ValidationUtility validationUtility, EntitiesDbService entitiesDbService, AssetsDbService assetsDbService, AssetOwnersRepository assetOwnersRepository) {
        return new UniverseCsvImporter(validationUtility, entitiesDbService, assetsDbService, assetOwnersRepository);
    }

    @Bean
    UniverseImportScheduler universeImportScheduler(UniverseCsvImporter universeCsvImporter, CacheManager cacheManager) throws IOException {
        return new UniverseImportScheduler(universeCsvImporter, cacheManager);
    }

    @Bean
    RedisSyncScheduler redisSyncScheduler() {
        return new RedisSyncScheduler();
    }

}
