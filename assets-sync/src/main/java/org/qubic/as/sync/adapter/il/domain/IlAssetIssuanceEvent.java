package org.qubic.as.sync.adapter.il.domain;

public record IlAssetIssuanceEvent(String issuerId, String name, String numberOfShares, String transactionHash, long tick, int eventType) {
}
