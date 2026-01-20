package org.sma.security.auth.dto;

/**
 * DTO for JWT token response
 */
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String username;

    public JwtTokenDto() {
    }

    public JwtTokenDto(String accessToken, String username) {
        this.accessToken = accessToken;
        this.username = username;
        this.tokenType = "Bearer";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
