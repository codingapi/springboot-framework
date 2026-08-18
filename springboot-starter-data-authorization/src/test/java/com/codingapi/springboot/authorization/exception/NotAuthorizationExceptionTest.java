package com.codingapi.springboot.authorization.exception;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * NotAuthorizationException 单元测试
 */
class NotAuthorizationExceptionTest {

    @Test
    void testDefaultConstructor() {
        NotAuthorizationException exception = new NotAuthorizationException();
        assertTrue(exception instanceof SQLException);
        assertNull(exception.getMessage());
    }

    @Test
    void testConstructorWithReason() {
        NotAuthorizationException exception = new NotAuthorizationException("no permission");
        assertTrue(exception instanceof SQLException);
        assertEquals("no permission", exception.getMessage());
    }
}
