package org.qubic.as.sync.domain;

public record EventHeader(int epoch, long tick, String eventId, String eventDigest) {
}
