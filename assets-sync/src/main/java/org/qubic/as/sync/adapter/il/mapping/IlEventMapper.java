package org.qubic.as.sync.adapter.il.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.qubic.as.sync.adapter.il.domain.IlAssetChangeEvent;
import org.qubic.as.sync.adapter.il.domain.IlAssetEvents;
import org.qubic.as.sync.adapter.il.domain.IlAssetIssuanceEvent;
import org.qubic.as.sync.domain.AssetChangeEvent;
import org.qubic.as.sync.domain.AssetEvents;
import org.qubic.as.sync.domain.AssetIssuanceEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, typeConversionPolicy = ReportingPolicy.WARN)
public interface IlEventMapper {

    @Mapping(target = "transferEvents", source = "changeEvents")
    AssetEvents map(IlAssetEvents source);

    @Mapping(target = "source", source = "sourceId")
    @Mapping(target = "destination", source = "destinationId")
    @Mapping(target = "issuer", source = "issuerId")
    @Mapping(target = "assetName", source = "name")
    @Mapping(target = "tickNumber", source = "tick")
    AssetChangeEvent map(IlAssetChangeEvent source);

    @Mapping(target = "issuer", source = "issuerId")
    @Mapping(target = "assetName", source = "name")
    @Mapping(target = "tickNumber", source = "tick")
    AssetIssuanceEvent map(IlAssetIssuanceEvent source);

}
