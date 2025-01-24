package org.qubic.as.messages;

public record AssetIssuanceMessage(String issuer, String assetName, long numberOfShares, String transactionHash, long tickNumber) {
}
