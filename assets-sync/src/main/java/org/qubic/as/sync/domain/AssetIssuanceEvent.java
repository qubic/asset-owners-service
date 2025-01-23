package org.qubic.as.sync.domain;

public record AssetIssuanceEvent(String issuer, String assetName, long numberOfShares, String transactionHash, long tickNumber) {
}
