package org.qubic.as.sync.domain;

import java.util.List;

public record TransactionEvents(String txId, List<TransactionEvent> events) {
}
