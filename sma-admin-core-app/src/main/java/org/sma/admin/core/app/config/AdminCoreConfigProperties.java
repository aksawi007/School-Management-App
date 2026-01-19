package org.sma.admin.core.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Admin Core Service Configuration Properties
 * Loaded from application.properties with prefix: sma.app.admin
 */
@Configuration
@ConfigurationProperties(prefix = "sma.app.admin")
public class AdminCoreConfigProperties {

    private String name;
    private String version;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
