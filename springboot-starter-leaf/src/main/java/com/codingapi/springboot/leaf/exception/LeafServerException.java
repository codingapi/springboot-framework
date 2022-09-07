package com.codingapi.springboot.leaf.exception;

public class LeafServerException extends RuntimeException {

    public LeafServerException() {
    }

    public LeafServerException(String message) {
        super(message);
    }

    public LeafServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LeafServerException(Throwable cause) {
        super(cause);
    }

    public LeafServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
