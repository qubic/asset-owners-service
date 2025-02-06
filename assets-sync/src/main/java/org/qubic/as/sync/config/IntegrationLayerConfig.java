package org.qubic.as.sync.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.adapter.il.IntegrationCoreApiService;
import org.qubic.as.sync.adapter.il.IntegrationCoreRpcService;
import org.qubic.as.sync.adapter.il.IntegrationEventApiService;
import org.qubic.as.sync.adapter.il.mapping.IlCoreMapper;
import org.qubic.as.sync.adapter.il.mapping.IlEventMapper;
import org.qubic.as.sync.properties.IntegrationClientProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Slf4j
@ConditionalOnProperty(value = "backend", havingValue = "integration", matchIfMissing = true)
@Configuration
public class IntegrationLayerConfig {

    @ConfigurationProperties(prefix = "il.event.client", ignoreUnknownFields = false)
    @Bean(name="eventClientProperties")
    IntegrationClientProperties eventClientProperties() {
        return new IntegrationClientProperties();
    }

    @ConfigurationProperties(prefix = "il.core.client", ignoreUnknownFields = false)
    @Bean(name="coreClientProperties")
    IntegrationClientProperties coreClientProperties() {
        return new IntegrationClientProperties();
    }

    @Bean(name="coreClient")
    WebClient coreApiWebClient(WebClient.Builder builder) {
        HttpClient httpClient = createHttpClient();
        URI uri = createUri(coreClientProperties());
        log.info("Integration layer core API url: {}", uri);
        return createClient(builder, httpClient, uri);
    }

    @Bean(name="eventClient")
    WebClient eventApiWebClient(WebClient.Builder builder) {
        HttpClient httpClient = createHttpClient();
        URI uri = createUri(eventClientProperties());
        log.info("Integration layer event API url: {}", uri);
        return createClient(builder, httpClient, uri);
    }

    @Bean
    IntegrationEventApiService integrationEventApiService(@Qualifier("eventClient") WebClient webClient, IlEventMapper eventMapper) {
        int retries = eventClientProperties().getRetries();
        return new IntegrationEventApiService(webClient, eventMapper, retries);
    }

    @ConditionalOnProperty(value = "il.core.use-go-nodes-format", havingValue = "false", matchIfMissing = true)
    @Bean
    CoreApiService integrationCoreRpcService(@Qualifier("coreClient") WebClient integrationApiWebClient, IlCoreMapper ilCoreMapper) {
        int retries = coreClientProperties().getRetries();
        return new IntegrationCoreRpcService(integrationApiWebClient, ilCoreMapper, retries);
    }

    @ConditionalOnProperty(value = "il.core.use-go-nodes-format", havingValue = "true")
    @Bean
    CoreApiService integrationCoreApiService(@Qualifier("coreClient") WebClient integrationApiWebClient, IlCoreMapper ilCoreMapper) {
        int retries = coreClientProperties().getRetries();
        return new IntegrationCoreApiService(integrationApiWebClient, ilCoreMapper, retries);
    }

    // helper methods
    private static HttpClient createHttpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3));
    }

    private static URI createUri(IntegrationClientProperties properties) {
        return UriComponentsBuilder.newInstance()
                .scheme(properties.getScheme())
                .host(properties.getHost())
                .port(StringUtils.stripToNull(properties.getPort()))
                .path(StringUtils.stripToNull(properties.getPath()))
                .build()
                .toUri();
    }

    private static WebClient createClient(WebClient.Builder builder, HttpClient httpClient, URI uri) {
        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(uri.toString())
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

}
