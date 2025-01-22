package org.qubic.as.sync.adapter.il.domain;

import org.qubic.as.sync.domain.EpochAndTick;

public record IlEventStatusResponse(EpochAndTick lastProcessedTick) {

}
