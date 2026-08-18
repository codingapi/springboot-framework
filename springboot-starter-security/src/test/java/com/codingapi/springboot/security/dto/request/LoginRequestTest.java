package com.codingapi.springboot.security.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginRequest 单元测试
 * <p>
 * 覆盖 isEmpty 校验、字符串取值以及 getBoolean 的默认值回退逻辑。
 */
class LoginRequestTest {

    @Test
    void isEmptyChecksUsernameAndPassword() {
        LoginRequest request = new LoginRequest();
        assertTrue(request.isEmpty());

        request.put("username", "admin");
        assertTrue(request.isEmpty());

        request.put("password", "123456");
        assertFalse(request.isEmpty());
        assertEquals("admin", request.getUsername());
        assertEquals("123456", request.getPassword());
        assertEquals("admin", request.getString("username"));
        assertNull(request.getString("missing"));
    }

    @Test
    void getBooleanReturnsValueWhenPresent() {
        LoginRequest request = new LoginRequest();
        request.put("remember", Boolean.TRUE);

        assertTrue(request.getBoolean("remember", false));
    }

    @Test
    void getBooleanReturnsDefaultWhenTypeMismatch() {
        LoginRequest request = new LoginRequest();
        request.put("text", "not-a-boolean");

        assertFalse(request.getBoolean("text", false));
        assertTrue(request.getBoolean("text", true));
    }

    @Test
    void getBooleanReturnsDefaultWhenKeyMissing() {
        LoginRequest request = new LoginRequest();

        assertTrue(request.getBoolean("missing", true));
        assertFalse(request.getBoolean("missing", false));
    }

}
