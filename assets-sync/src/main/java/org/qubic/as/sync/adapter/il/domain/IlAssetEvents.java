package org.qubic.as.sync.adapter.il.domain;

import java.util.List;

public record IlAssetEvents(long latestTick, List<IlAssetChangeEvent> changeEvents, List<IlAssetIssuanceEvent> issuanceEvents) {

}
