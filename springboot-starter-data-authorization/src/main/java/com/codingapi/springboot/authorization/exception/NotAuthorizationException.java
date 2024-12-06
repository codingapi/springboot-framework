package com.codingapi.springboot.authorization.exception;

import java.sql.SQLException;

public class NotAuthorizationException extends SQLException {

    public NotAuthorizationException() {
    }

    public NotAuthorizationException(String reason) {
        super(reason);
    }
}
