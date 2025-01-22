package org.qubic.as.sync.adapter.il;

import org.junit.jupiter.api.Test;
import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.domain.TickInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

class IntegrationCoreApiServiceIT extends AbstractIntegrationApiTest {

    private static final String TICK_INFO_RESPONSE = """
            {
              "tick": 123,
              "durationInSeconds": 1,
              "epoch": 456,
              "numberOfAlignedVotes": 2,
              "numberOfMisalignedVotes": 3,
              "initialTickOfEpoch": 99
            }""";

    @Autowired
    private CoreApiService apiService;

    @Test
    void getCurrentTick() {
        prepareResponse(response -> response
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(TICK_INFO_RESPONSE));

        StepVerifier.create(apiService.getTickInfo())
                .expectNext(new TickInfo(456, 123, 99))
                .verifyComplete();

        assertRequest("/v1/core/getTickInfo");
    }

}