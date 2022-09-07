package com.codingapi.springboot.security.exception;

public class TokenExpiredException extends Exception {

    public TokenExpiredException(String message) {
        super(message);
    }
}
