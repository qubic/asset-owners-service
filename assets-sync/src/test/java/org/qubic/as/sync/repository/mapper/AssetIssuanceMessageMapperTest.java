package org.qubic.as.sync.repository.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.qubic.as.sync.domain.AssetIssuanceEvent;

import static org.assertj.core.api.Assertions.assertThat;

class AssetIssuanceMessageMapperTest {

    private final AssetIssuanceMessageMapper mapper = Mappers.getMapper(AssetIssuanceMessageMapper.class);

    @Test
    void map() {
        AssetIssuanceEvent event = new AssetIssuanceEvent(
                "issuer",
                "assetName",
                1,
                "transactionHash",
                2);

        AssetIssuanceMessage mapped = mapper.map(event);

        assertThat(mapped).isEqualTo(
                new AssetIssuanceMessage(
                        "issuer",
                        "assetName",
                        1,
                        "transactionHash",
                        2)
        );
    }

}