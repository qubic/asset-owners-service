package org.qubic.as.sync.adapter.il.mapping;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.qubic.as.sync.adapter.il.domain.IlAssetChangeEvent;
import org.qubic.as.sync.adapter.il.domain.IlAssetEvents;
import org.qubic.as.sync.adapter.il.domain.IlAssetIssuanceEvent;
import org.qubic.as.sync.domain.AssetChangeEvent;
import org.qubic.as.sync.domain.AssetEvents;
import org.qubic.as.sync.domain.AssetIssuanceEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IlEventMapperTest {

    private final IlEventMapper mapper = Mappers.getMapper(IlEventMapper.class);

    @Test
    void mapAssetEvents() {
        IlAssetChangeEvent changeEvent1 = new IlAssetChangeEvent("source", "destination", "issuer", "name", "123", "transactionHash", 234, 2);
        IlAssetChangeEvent changeEvent2 = new IlAssetChangeEvent("source", "destination", "issuer", "name", "234", "transactionHash", 456, 3);
        IlAssetIssuanceEvent issuanceEvent = new IlAssetIssuanceEvent("issuer", "name", "123456", "transactionHash", 234, 1);
        IlAssetEvents events = new IlAssetEvents(12345, List.of(changeEvent1, changeEvent2), List.of(issuanceEvent));
        AssetEvents result = mapper.map(events);
        assertThat(result.latestTick()).isEqualTo(12345);
        assertThat(result.transferEvents()).hasSize(2);
        assertThat(result.issuanceEvents()).hasSize(1);
    }

    @Test
    void mapAssetChangeEvent() {
        IlAssetChangeEvent changeEvent = new IlAssetChangeEvent("source", "destination", "issuer", "name", "123", "transactionHash", 234, 2);

        AssetChangeEvent result = mapper.map(changeEvent);
        assertThat(result.source()).isEqualTo("source");
        assertThat(result.destination()).isEqualTo("destination");
        assertThat(result.issuer()).isEqualTo("issuer");
        assertThat(result.assetName()).isEqualTo("name");
        assertThat(result.numberOfShares()).isEqualTo(123);
        assertThat(result.transactionHash()).isEqualTo("transactionHash");
        assertThat(result.tickNumber()).isEqualTo(234);
        assertThat(result.eventType()).isEqualTo(2);
    }

    @Test
    void mapAssetIssuanceEvent() {
        IlAssetIssuanceEvent issuanceEvent = new IlAssetIssuanceEvent("issuer", "name", "123456", "transactionHash", 234, 1);

        AssetIssuanceEvent result = mapper.map(issuanceEvent);
        assertThat(result.issuer()).isEqualTo("issuer");
        assertThat(result.assetName()).isEqualTo("name");
        assertThat(result.numberOfShares()).isEqualTo(123456);
        assertThat(result.transactionHash()).isEqualTo("transactionHash");
        assertThat(result.tickNumber()).isEqualTo(234);
    }


}