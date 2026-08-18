package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.framework.exception.LocaleMessageException;
import com.codingapi.springboot.security.crypto.SecurityCryptoConfiguration;
import com.codingapi.springboot.security.exception.TokenExpiredException;
import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JwtTokenGateway 单元测试
 * <p>
 * 覆盖各 create 重载与 JWT 签发/解析的往返逻辑，以及非法 token 的异常分支。
 */
class JwtTokenGatewayTest {

    private JwtTokenGateway gateway;

    @BeforeAll
    static void initCrypto() throws Exception {
        // Token 构造时会通过 AESTools 加密 iv，单元测试环境需要先完成初始化
        new SecurityCryptoConfiguration().aes(new CodingApiSecurityProperties());
    }

    @BeforeEach
    void setUp() {
        gateway = new JwtTokenGateway(new SecurityJWTProperties());
    }

    @Test
    void createWithAllParamsAndParserRoundTrip() throws TokenExpiredException {
        List<String> authorities = Arrays.asList("ADMIN", "USER");
        TestVO extra = new TestVO("test-name");

        Token token = gateway.create("admin", "123456", authorities, extra.toJson());
        assertNotNull(token.getToken());
        token.verify();

        Token parsed = gateway.parser(token.getToken());
        assertEquals("admin", parsed.getUsername());
        assertEquals("123456", parsed.decodeIv());
        assertEquals(authorities, parsed.getAuthorities());
        assertEquals("test-name", parsed.parseExtra(TestVO.class).getName());
    }

    @Test
    void createOverloadWithAuthoritiesOnly() {
        List<String> authorities = Collections.singletonList("ADMIN");

        Token token = gateway.create("admin", authorities);

        Token parsed = gateway.parser(token.getToken());
        assertEquals("admin", parsed.getUsername());
        assertEquals(authorities, parsed.getAuthorities());
    }

    @Test
    void createOverloadWithAuthoritiesAndExtra() {
        List<String> authorities = Collections.singletonList("ADMIN");
        TestVO extra = new TestVO("extra-name");

        Token token = gateway.create("admin", authorities, extra.toJson());

        Token parsed = gateway.parser(token.getToken());
        assertEquals("admin", parsed.getUsername());
        assertEquals("extra-name", parsed.parseExtra(TestVO.class).getName());
    }

    @Test
    void createOverloadWithIvAndAuthorities() {
        List<String> authorities = Collections.singletonList("ADMIN");

        Token token = gateway.create("admin", "123456", authorities);

        Token parsed = gateway.parser(token.getToken());
        assertEquals("admin", parsed.getUsername());
        assertEquals("123456", parsed.decodeIv());
    }

    @Test
    void parserWithInvalidTokenThrowsLocaleMessageException() {
        assertThrows(LocaleMessageException.class, () -> gateway.parser("invalid.jwt.token"));
    }

    @Test
    void parserWithTokenSignedByOtherKeyThrowsException() {
        SecurityJWTProperties otherProperties = new SecurityJWTProperties();
        otherProperties.setSecretKey("another-secret-key-must-longer-than-32-chars");
        JwtTokenGateway otherGateway = new JwtTokenGateway(otherProperties);
        Token token = otherGateway.create("admin", Collections.singletonList("ADMIN"));

        assertThrows(LocaleMessageException.class, () -> gateway.parser(token.getToken()));
    }

}
