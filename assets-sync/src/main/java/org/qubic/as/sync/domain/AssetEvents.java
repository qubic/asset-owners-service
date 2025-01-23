package org.qubic.as.sync.domain;

import java.util.List;

public record AssetEvents(long latestTick, List<AssetChangeEvent> transferEvents, List<AssetIssuanceEvent> issuanceEvents) {
}
