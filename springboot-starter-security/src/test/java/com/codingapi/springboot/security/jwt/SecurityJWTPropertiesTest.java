package com.codingapi.springboot.security.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SecurityJWTProperties 单元测试
 * <p>
 * 校验默认值与 setter/getter 行为。
 */
class SecurityJWTPropertiesTest {

    @Test
    void defaultValues() {
        SecurityJWTProperties properties = new SecurityJWTProperties();

        assertTrue(properties.isEnable());
        assertEquals("codingapi.security.jwt.secretkey", properties.getSecretKey());
        assertEquals(900000, properties.getValidTime());
        assertEquals(600000, properties.getRestTime());
    }

    @Test
    void settersAndGetters() {
        SecurityJWTProperties properties = new SecurityJWTProperties();
        properties.setEnable(false);
        properties.setSecretKey("changed-secret-key");
        properties.setValidTime(1000);
        properties.setRestTime(500);

        assertFalse(properties.isEnable());
        assertEquals("changed-secret-key", properties.getSecretKey());
        assertEquals(1000, properties.getValidTime());
        assertEquals(500, properties.getRestTime());
    }

}
