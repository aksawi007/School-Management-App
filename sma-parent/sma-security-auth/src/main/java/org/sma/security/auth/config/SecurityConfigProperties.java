package org.sma.security.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for security module
 */
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityConfigProperties {

    private String jwtSecret = "mySecretKeyForJWTTokenGenerationAndValidationPurpose123456789";
    private long jwtExpirationInMs = 86400000; // 24 hours
    private long jwtRefreshExpirationInMs = 604800000; // 7 days
    private String[] excludePatterns = {"/auth/**", "/public/**", "/actuator/**"};
    private boolean enableCors = true;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }

    public void setJwtExpirationInMs(long jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public long getJwtRefreshExpirationInMs() {
        return jwtRefreshExpirationInMs;
    }

    public void setJwtRefreshExpirationInMs(long jwtRefreshExpirationInMs) {
        this.jwtRefreshExpirationInMs = jwtRefreshExpirationInMs;
    }

    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public boolean isEnableCors() {
        return enableCors;
    }

    public void setEnableCors(boolean enableCors) {
        this.enableCors = enableCors;
    }
}
