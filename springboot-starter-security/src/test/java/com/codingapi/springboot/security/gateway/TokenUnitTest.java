package com.codingapi.springboot.security.gateway;

import com.codingapi.springboot.security.crypto.SecurityCryptoConfiguration;
import com.codingapi.springboot.security.exception.TokenExpiredException;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Token 单元测试
 * <p>
 * 覆盖构造、iv 加解密往返、过期校验、重置判断、extra 解析、
 * 认证凭据转换等逻辑。
 */
class TokenUnitTest {

    @BeforeAll
    static void initCrypto() throws Exception {
        // Token 构造时会通过 AESTools 加密 iv，单元测试环境需要先完成初始化
        new SecurityCryptoConfiguration().aes(new CodingApiSecurityProperties());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void constructorWithIvEncryptsAndDecodeRoundTrip() {
        List<String> authorities = Arrays.asList("ADMIN", "USER");
        Token token = new Token("admin", "123456", "{\"name\":\"test\"}", authorities, 900000, 600000);

        assertEquals("admin", token.getUsername());
        assertEquals("123456", token.decodeIv());
        assertEquals(authorities, token.getAuthorities());
        assertFalse(token.isExpire());
        assertEquals("test", token.parseExtra(Map.class).get("name"));
    }

    @Test
    void nullIvAndNullExtraReturnNull() {
        Token token = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, 600000);

        assertNull(token.decodeIv());
        assertNull(token.parseExtra(Map.class));
    }

    @Test
    void expiredTokenVerifyThrowsException() {
        Token expired = new Token("admin", null, null, Collections.singletonList("ADMIN"), -1000, -1000);

        assertTrue(expired.isExpire());
        assertFalse(expired.canRestToken());
        TokenExpiredException exception = assertThrows(TokenExpiredException.class, expired::verify);
        assertEquals("token expired.", exception.getMessage());
    }

    @Test
    void validTokenVerifyPassesAndCannotRest() throws TokenExpiredException {
        Token token = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, 600000);

        token.verify();
        assertFalse(token.canRestToken());
    }

    @Test
    void restableTokenWhenRemindTimeReached() {
        Token token = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, -1000);

        assertFalse(token.isExpire());
        assertTrue(token.canRestToken());
    }

    @Test
    void getAuthenticationTokenBuildsAuthoritiesAndPushesExtra() {
        Token token = new Token("admin", "iv-value", "{\"channel\":\"pc\"}",
                Arrays.asList("ADMIN", "USER"), 900000, 600000);

        UsernamePasswordAuthenticationToken authentication = token.getAuthenticationToken();

        assertSame(token, authentication.getPrincipal());
        assertEquals(2, authentication.getAuthorities().size());
        assertEquals("{\"channel\":\"pc\"}", TokenContext.getExtra());
    }

    @Test
    void defaultConstructorAndSetters() {
        Token token = new Token();
        token.setUsername("user");
        token.setToken("token-value");
        token.setExtra("{}");
        token.setAuthorities(Collections.singletonList("USER"));
        token.setExpireTime(System.currentTimeMillis() + 900000);
        token.setRemindTime(System.currentTimeMillis() + 600000);

        assertEquals("user", token.getUsername());
        assertEquals("token-value", token.getToken());
        assertEquals("{}", token.getExtra());
        assertFalse(token.isExpire());
        assertFalse(token.canRestToken());
    }

}
