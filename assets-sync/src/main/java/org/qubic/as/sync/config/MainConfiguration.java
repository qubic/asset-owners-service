package org.qubic.as.sync.config;

import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.adapter.EventApiService;
import org.qubic.as.sync.job.SyncJob;
import org.qubic.as.sync.job.SyncJobRunner;
import org.qubic.as.sync.properties.SyncJobProperties;
import org.qubic.as.sync.repository.TickRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

    @ConfigurationProperties(prefix = "job.sync", ignoreUnknownFields = false)
    @Bean
    SyncJobProperties syncJobProperties() {
        return new SyncJobProperties();
    }

    @Bean
    SyncJob syncJob(CoreApiService coreService, EventApiService eventService,
                    TickRepository tickRepository) {
        return new SyncJob(coreService, eventService, tickRepository);
    }

    @Bean
    SyncJobRunner syncJobRunner(SyncJob syncJob, SyncJobProperties syncJobProperties) {
        return new SyncJobRunner(syncJob, syncJobProperties);
    }

}
