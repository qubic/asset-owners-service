package org.qubic.as.sync.repository.mapper;

import org.mapstruct.Mapper;
import org.qubic.as.messages.AssetChangeMessage;
import org.qubic.as.sync.domain.AssetChangeEvent;

@Mapper(componentModel = "spring")
public interface AssetChangeMessageMapper {

    AssetChangeMessage map(AssetChangeEvent event);

}
