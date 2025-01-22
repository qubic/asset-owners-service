package org.qubic.as.sync.adapter.il.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.qubic.as.sync.adapter.il.domain.IlTickInfo;
import org.qubic.as.sync.domain.TickInfo;

@Mapper(componentModel = "spring")
public interface IlCoreMapper {

    @Mapping(target = "initialTick", source= "initialTickOfEpoch")
    TickInfo map(IlTickInfo source);

}
