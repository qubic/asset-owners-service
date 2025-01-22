package org.qubic.aos.api.config;

import org.qubic.aos.api.db.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(basePackageClasses = DatabaseRepositories.class)
public class DatabaseConfiguration {

    @Bean
    AssetsDbService assetsDbService(AssetsRepository assetsRepository) {
        return new AssetsDbService(assetsRepository);
    }

    @Bean
    EntitiesDbService entitiesDbService(EntitiesRepository entitiesRepository) {
        return new EntitiesDbService(entitiesRepository);
    }

}
