package com.codingapi.springboot.security.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * MyLogoutHandler 单元测试
 * <p>
 * logout 为空实现，仅验证调用不抛异常。
 */
class MyLogoutHandlerTest {

    @Test
    void logoutDoesNotThrow() {
        assertDoesNotThrow(() -> new MyLogoutHandler()
                .logout(new MockHttpServletRequest(), new MockHttpServletResponse(), null));
    }

}
