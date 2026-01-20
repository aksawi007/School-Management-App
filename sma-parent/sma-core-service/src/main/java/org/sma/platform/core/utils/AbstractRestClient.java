package org.sma.platform.core.utils;

import org.sma.platform.core.config.RestClientInstanceConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Base class for per-client REST clients.
 * Extend this class and annotate the concrete subclass with
 * `@Component` and `@ConfigurationProperties(prefix = "sma.client.<name>")`.
 */
public abstract class AbstractRestClient extends RestClientInstanceConfig {

    @Autowired
    private RestClientFactory restClientFactory;

    protected RestClientInstance client;

    @PostConstruct
    public void initRestClient() {
        this.client = restClientFactory.create(this);
    }

    protected RestClientInstance getClient() {
        return client;
    }
}
