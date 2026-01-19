package org.sma.platform.core.exception;

public class SmaException extends RuntimeException {

    public SmaException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmaException(String message) {
        super(message);
    }

}
