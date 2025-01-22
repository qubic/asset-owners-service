package org.qubic.as.sync.config;

import org.qubic.as.sync.job.SyncJob;
import org.qubic.as.sync.job.SyncJobRunner;
import org.qubic.as.sync.properties.IntegrationClientProperties;
import org.qubic.as.sync.properties.SyncJobProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

    @ConfigurationProperties(prefix = "il.event.client", ignoreUnknownFields = false)
    @Bean(name="eventClientProperties")
    IntegrationClientProperties eventClientProperties() {
        return new IntegrationClientProperties();
    }

    @ConfigurationProperties(prefix = "job.sync", ignoreUnknownFields = false)
    @Bean
    SyncJobProperties syncJobProperties() {
        return new SyncJobProperties();
    }

    @Bean
    SyncJob syncJob() {
        return new SyncJob();
    }

    @Bean
    SyncJobRunner syncJobRunner(SyncJob syncJob, SyncJobProperties syncJobProperties) {
        return new SyncJobRunner(syncJob, syncJobProperties);
    }

}
