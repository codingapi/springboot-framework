package com.codingapi.springboot.security.redis;

import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * RedisTokenGatewayImpl 单元测试
 * <p>
 * 验证其对 RedisTokenGateway 的委托行为以及 TokenGateway 默认方法。
 */
class RedisTokenGatewayImplTest {

    private RedisTokenGateway redisTokenGateway;
    private RedisTokenGatewayImpl gatewayImpl;

    @BeforeEach
    void setUp() {
        redisTokenGateway = mock(RedisTokenGateway.class);
        gatewayImpl = new RedisTokenGatewayImpl(redisTokenGateway);
    }

    @Test
    void createDelegatesToRedisTokenGateway() {
        List<String> authorities = Collections.singletonList("ADMIN");
        Token expected = new Token("admin", null, null, authorities, 900000, 600000);
        when(redisTokenGateway.create("admin", "123456", authorities, "extra")).thenReturn(expected);

        Token token = gatewayImpl.create("admin", "123456", authorities, "extra");

        assertSame(expected, token);
    }

    @Test
    void parserDelegatesToGetToken() {
        List<String> authorities = Collections.singletonList("ADMIN");
        Token expected = new Token("admin", null, null, authorities, 900000, 600000);
        when(redisTokenGateway.getToken("token-sign")).thenReturn(expected);

        Token token = gatewayImpl.parser("token-sign");

        assertSame(expected, token);
    }

    @Test
    void tokenGatewayDefaultMethodsDelegateToFullCreate() {
        List<String> authorities = Collections.singletonList("ADMIN");
        Token expected = new Token("user", null, null, authorities, 900000, 600000);
        when(redisTokenGateway.create("user", null, authorities, null)).thenReturn(expected);

        TokenGateway gateway = gatewayImpl;
        assertSame(expected, gateway.create("user", authorities));
        assertSame(expected, gateway.create("user", authorities, null));
        assertSame(expected, gateway.create("user", null, authorities));
        assertTrue(gateway instanceof RedisTokenGatewayImpl);
    }

}
