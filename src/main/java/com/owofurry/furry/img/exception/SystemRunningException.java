package com.owofurry.furry.img.exception;

public class SystemRunningException extends RuntimeException {
    public SystemRunningException() {
    }

    public SystemRunningException(String message) {
        super(message);
    }

    public SystemRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemRunningException(Throwable cause) {
        super(cause);
    }

    public SystemRunningException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
