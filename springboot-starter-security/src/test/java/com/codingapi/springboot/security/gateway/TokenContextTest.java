package com.codingapi.springboot.security.gateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * TokenContext 单元测试
 * <p>
 * 覆盖 ThreadLocal extra 的读写以及从 SecurityContext 中获取当前 Token。
 */
class TokenContextTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void pushExtraAndGetExtra() {
        TokenContext.pushExtra("extra-value");
        assertEquals("extra-value", TokenContext.getExtra());

        TokenContext.pushExtra(null);
        assertNull(TokenContext.getExtra());
    }

    @Test
    void currentReturnsTokenFromSecurityContext() {
        Token token = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, 600000);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(token, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertSame(token, TokenContext.current());
    }

}
