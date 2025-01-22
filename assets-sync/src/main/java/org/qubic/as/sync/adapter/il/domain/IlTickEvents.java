package org.qubic.as.sync.adapter.il.domain;

import org.qubic.as.sync.domain.TransactionEvents;

import java.util.List;

public record IlTickEvents(long tick, List<TransactionEvents> txEvents) {
}
