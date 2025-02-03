package org.qubic.as.sync.properties;

import lombok.Data;

@Data
public class IntegrationClientProperties {

    private String scheme;
    private String host;
    private String port;
    private String path;
    private int retries;

}
