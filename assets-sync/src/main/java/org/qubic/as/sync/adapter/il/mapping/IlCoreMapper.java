package org.qubic.as.sync.adapter.il.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.qubic.as.sync.adapter.il.domain.IlApiTickInfo;
import org.qubic.as.sync.adapter.il.domain.IlRpcTickInfo;
import org.qubic.as.sync.domain.TickInfo;

@Mapper(componentModel = "spring")
public interface IlCoreMapper {

    @Mapping(target = "initialTick", source= "initialTickOfEpoch")
    TickInfo map(IlApiTickInfo source);

    TickInfo map(IlRpcTickInfo source);

}
