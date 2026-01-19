package org.sma.admin.core.app.exception;

/**
 * Custom exception for Admin Core Service domain
 */
public class SmaAdminException extends RuntimeException {
    
    public SmaAdminException(String message) {
        super(message);
    }

    public SmaAdminException(String message, Throwable cause) {
        super(message, cause);
    }
}
