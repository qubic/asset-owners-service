package org.qubic.as.sync.repository.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.sync.domain.AssetChangeEvent;

import static org.assertj.core.api.Assertions.*;

class AssetChangeMessageMapperTest {

    private final AssetChangeMessageMapper mapper = Mappers.getMapper(AssetChangeMessageMapper.class);

    @Test
    void map() {
        AssetChangeEvent event = new AssetChangeEvent("source",
                "destination",
                "issuer",
                "assetName",
                1,
                "transactionHash",
                2,
                3);

        AssetChangeMessage mapped = mapper.map(event);

        assertThat(mapped).isEqualTo(
                new AssetChangeMessage("source",
                        "destination",
                        "issuer",
                        "assetName",
                        1,
                        "transactionHash",
                        2,
                        3)
        );


    }

}