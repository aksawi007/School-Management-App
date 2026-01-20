package org.sma.platform.core.config;

/**
 * Generic per-client rest client configuration (no prefix here).
 * Clients should extend this class and annotate with @ConfigurationProperties(prefix=...)
 */
public class RestClientInstanceConfig {
    private String baseUrl;
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 10000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }
}
