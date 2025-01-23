package org.qubic.as.sync.adapter.il;

import io.micrometer.core.instrument.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.qubic.as.sync.domain.AssetChangeEvent;
import org.qubic.as.sync.domain.AssetEvents;
import org.qubic.as.sync.domain.AssetIssuanceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationEventApiServiceIT extends AbstractIntegrationApiTest {

    @Autowired
    private IntegrationEventApiService apiClient;

    @Test
    void getTickEvents() {
        String responseJson = IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream(
                "/test-data/il/get-tick-events-0-response.json"
        )), StandardCharsets.UTF_8);

        prepareResponse(response -> response
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseJson));

        AssetEvents assetEvents = apiClient.getTickEvents(123456).block();
        assertThat(assetEvents).isNotNull();
        assertThat(assetEvents.latestTick()).isEqualTo(18636999);
        List<AssetChangeEvent> transferEvents = assetEvents.transferEvents();
        List<AssetIssuanceEvent> issuanceEvents = assetEvents.issuanceEvents();

        assertThat(transferEvents).hasSize(2);
        assertThat(issuanceEvents).hasSize(1);

        assertThat(transferEvents).containsExactly(
                new AssetChangeEvent("GIEXGSAGFPAPMCHKZQJRMIUWPIDDFPHHDPJHQOQLPBEMJHWJAKHUXNEECSSK",
                        "MMXGLHFLKCROMEJSSDYIMABZJAXBICQBLYKSGXAQJGUDOKMBSTPQPAREFBML",
                        "QCAPWMYRSHLBJHSTTZQVCIBARVOASKDENASAKNOBRGPFWWKRCUVUAXYEZVOG",
                        "QCAP",
                        20,
                        "pnanppagyakeofwxvgiqmiokuvodhomhyyhgjckekajxtjgcywmmsbzaesfo",
                        18636419,
                        2),
                new AssetChangeEvent("GIEXGSAGFPAPMCHKZQJRMIUWPIDDFPHHDPJHQOQLPBEMJHWJAKHUXNEECSSK",
                        "MMXGLHFLKCROMEJSSDYIMABZJAXBICQBLYKSGXAQJGUDOKMBSTPQPAREFBML",
                        "QCAPWMYRSHLBJHSTTZQVCIBARVOASKDENASAKNOBRGPFWWKRCUVUAXYEZVOG",
                        "QCAP",
                        20,
                        "pnanppagyakeofwxvgiqmiokuvodhomhyyhgjckekajxtjgcywmmsbzaesfo",
                        18636419,
                        3)
        );

        assertThat(issuanceEvents).containsExactly(
                new AssetIssuanceEvent("QCAPWMYRSHLBJHSTTZQVCIBARVOASKDENASAKNOBRGPFWWKRCUVUAXYEZVOG",
                        "QCAP",
                        21000000,
                        "roovqujtgcrnvfmduefbsfjavrgfajiqhpyarxhjjdqdmydnfszrgbcbiwso",
                        18636419)
        );

        assertRequest("/v1/ticks/123456/events/assets");
    }

    @Test
    void getLatestProcessedTick() {
        String responseJson = IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream(
                "/test-data/il/get-tick-events-0-response.json"
        )), StandardCharsets.UTF_8);

        prepareResponse(response -> response
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseJson));

        Long tickNumber = apiClient.getLatestTick().block();
        assertThat(tickNumber).isNotNull();
        assertThat(tickNumber).isEqualTo(18636999);

        assertRequest("/v1/ticks/0/events/assets");
    }

}