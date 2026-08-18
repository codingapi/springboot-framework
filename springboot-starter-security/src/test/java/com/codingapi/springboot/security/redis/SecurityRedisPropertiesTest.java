package com.codingapi.springboot.security.redis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SecurityRedisProperties 单元测试
 * <p>
 * 校验默认值与 setter/getter 行为。
 */
class SecurityRedisPropertiesTest {

    @Test
    void defaultValues() {
        SecurityRedisProperties properties = new SecurityRedisProperties();

        assertTrue(properties.isEnable());
        assertEquals(900000, properties.getValidTime());
        assertEquals(600000, properties.getRestTime());
    }

    @Test
    void settersAndGetters() {
        SecurityRedisProperties properties = new SecurityRedisProperties();
        properties.setEnable(false);
        properties.setValidTime(1000);
        properties.setRestTime(500);

        assertFalse(properties.isEnable());
        assertEquals(1000, properties.getValidTime());
        assertEquals(500, properties.getRestTime());
    }

}
