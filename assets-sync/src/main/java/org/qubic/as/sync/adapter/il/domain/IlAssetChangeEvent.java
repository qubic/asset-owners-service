package org.qubic.as.sync.adapter.il.domain;

public record IlAssetChangeEvent(String sourceId, String destinationId, String issuerId, String name, String numberOfShares, String transactionHash, long tick, int eventType) {
}
