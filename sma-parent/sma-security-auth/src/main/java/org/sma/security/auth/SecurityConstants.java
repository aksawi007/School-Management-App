package org.sma.security.auth;

/**
 * Security context constants and default values
 */
public final class SecurityConstants {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_GUEST = "GUEST";

    public static final String PERMISSION_READ = "READ";
    public static final String PERMISSION_CREATE = "CREATE";
    public static final String PERMISSION_UPDATE = "UPDATE";
    public static final String PERMISSION_DELETE = "DELETE";

    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_TYPE = "JWT";

    public static final long DEFAULT_EXPIRATION_TIME = 86400000; // 24 hours
    public static final long DEFAULT_REFRESH_EXPIRATION_TIME = 604800000; // 7 days

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
}
