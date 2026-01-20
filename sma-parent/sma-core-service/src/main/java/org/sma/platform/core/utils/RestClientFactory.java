package org.sma.platform.core.utils;

import org.sma.platform.core.config.RestClientInstanceConfig;
import org.springframework.stereotype.Component;

@Component
public class RestClientFactory {

    public RestClientInstance create(RestClientInstanceConfig config) {
        return new RestClientInstance(config.getBaseUrl(), config.getConnectTimeoutMs(), config.getReadTimeoutMs());
    }
}
