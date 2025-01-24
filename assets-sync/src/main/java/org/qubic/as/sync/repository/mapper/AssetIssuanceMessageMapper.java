package org.qubic.as.sync.repository.mapper;

import org.mapstruct.Mapper;
import org.qubic.as.messages.AssetIssuanceMessage;
import org.qubic.as.sync.domain.AssetIssuanceEvent;

@Mapper(componentModel = "spring")
public interface AssetIssuanceMessageMapper {

    AssetIssuanceMessage map(AssetIssuanceEvent event);

}
