package org.qubic.as.messages;

public record AssetChangeMessage(String source, String destination, String issuer, String assetName, long numberOfShares, String transactionHash, long tickNumber, int eventType) {
}
