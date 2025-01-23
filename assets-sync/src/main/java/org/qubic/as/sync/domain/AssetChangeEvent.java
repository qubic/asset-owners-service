package org.qubic.as.sync.domain;

public record AssetChangeEvent(String source, String destination, String issuer, String assetName, long numberOfShares, String transactionHash, long tickNumber, int eventType) {
}
