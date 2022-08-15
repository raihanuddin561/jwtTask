package com.exossystem.task.exceptions;

public class TokenValidationException extends Exception{
    public TokenValidationException() {
        super();
    }

    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenValidationException(Throwable cause) {
        super(cause);
    }

    protected TokenValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}