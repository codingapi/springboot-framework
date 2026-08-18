package com.codingapi.springboot.security.dto.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * LoginRequestContext 单元测试
 * <p>
 * 覆盖单例获取与 ThreadLocal 的 set/get/clean 行为。
 */
class LoginRequestContextTest {

    @AfterEach
    void tearDown() {
        LoginRequestContext.getInstance().clean();
    }

    @Test
    void getInstanceReturnsSingleton() {
        assertSame(LoginRequestContext.getInstance(), LoginRequestContext.getInstance());
    }

    @Test
    void setGetAndClean() {
        LoginRequestContext context = LoginRequestContext.getInstance();
        LoginRequest request = new LoginRequest();
        request.put("username", "admin");

        context.set(request);
        assertSame(request, context.get());

        context.clean();
        assertNull(context.get());
    }

}
