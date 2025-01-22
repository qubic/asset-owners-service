package org.qubic.as.sync.properties;

import lombok.Data;

import java.time.Duration;

@Data
public class SyncJobProperties {

    private boolean enabled;
    private long repeats;
    private Duration sleepInterval;
    private Duration retryInterval;

}
