package org.qubic.as.sync.adapter.il;

import org.junit.jupiter.api.Test;
import org.qubic.as.sync.adapter.CoreApiService;
import org.qubic.as.sync.domain.TickInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

class IntegrationCoreRpcServiceIT extends AbstractIntegrationApiTest {

    private static final String TICK_INFO_RESPONSE = """
            {
               "tickInfo": {
                 "tick": 18990293,
                 "duration": 2,
                 "epoch": 147,
                 "initialTick": 18964679
               }
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
                .expectNext(new TickInfo(147, 18990293, 18964679))
                .verifyComplete();

        assertRequest("/v1/tick-info");
    }

}