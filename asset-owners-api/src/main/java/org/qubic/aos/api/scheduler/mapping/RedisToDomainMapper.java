package org.qubic.aos.api.scheduler.mapping;

public interface RedisToDomainMapper<T, S> {

    T map(S source);

}
