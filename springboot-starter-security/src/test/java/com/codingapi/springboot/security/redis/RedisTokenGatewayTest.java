package com.codingapi.springboot.security.redis;

import com.codingapi.springboot.framework.crypto.AESUtils;
import com.codingapi.springboot.security.crypto.SecurityCryptoConfiguration;
import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RedisTokenGateway 单元测试
 * <p>
 * 通过 mock RedisTemplate/ValueOperations 验证 token 的创建、解析、删除、
 * 重置以及按用户名清理等逻辑，不依赖真实 Redis 连接。
 */
class RedisTokenGatewayTest {

    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private SecurityRedisProperties properties;
    private RedisTokenGateway gateway;

    @BeforeAll
    static void initCrypto() throws Exception {
        // Token 构造时会通过 AESTools 加密 iv，单元测试环境需要先完成初始化
        new SecurityCryptoConfiguration().aes(new CodingApiSecurityProperties());
    }

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        properties = new SecurityRedisProperties();
        gateway = new RedisTokenGateway(redisTemplate, properties);
    }

    @Test
    void createTokenStoresJsonWithValidTime() throws Exception {
        List<String> authorities = Arrays.asList("ADMIN", "USER");

        Token token = gateway.create("admin", "123456", authorities, "{\"name\":\"test\"}");

        assertNotNull(token.getToken());
        assertEquals("admin", token.getUsername());
        assertEquals("123456", token.decodeIv());
        assertEquals(authorities, token.getAuthorities());
        // key 由 AES 加密后的用户名 + ":" + 随机 UUID 组成
        String encodedUsername = AESUtils.getInstance().encode("admin");
        assertTrue(token.getToken().startsWith(encodedUsername + ":"));

        verify(valueOperations).set(eq(token.getToken()), eq(token.toJson()),
                eq((long) properties.getValidTime()), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void getTokenWhenPresent() {
        Token source = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, 600000);
        source.setToken("token-key");
        when(valueOperations.get("token-key")).thenReturn(source.toJson());

        Token token = gateway.getToken("token-key");

        assertNotNull(token);
        assertEquals("admin", token.getUsername());
        assertEquals(source.getAuthorities(), token.getAuthorities());
        assertEquals(source.getExpireTime(), token.getExpireTime());
    }

    @Test
    void getTokenWhenAbsentReturnsNull() {
        when(valueOperations.get("no-such-key")).thenReturn(null);

        assertNull(gateway.getToken("no-such-key"));
    }

    @Test
    void removeTokenDeletesKey() {
        gateway.removeToken("token-key");

        verify(redisTemplate).delete("token-key");
    }

    @Test
    void resetTokenWritesAgainWithValidTime() {
        Token token = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, 600000);
        token.setToken("token-key");

        gateway.resetToken(token);

        verify(valueOperations).set(eq("token-key"), eq(token.toJson()),
                eq((long) properties.getValidTime()), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void removeUsernameDeletesMatchedKeys() {
        Set<String> keys = new HashSet<String>(Arrays.asList("key-1", "key-2"));
        when(redisTemplate.keys(anyString())).thenReturn(keys);

        gateway.removeUsername("admin");

        ArgumentCaptor<String> patternCaptor = ArgumentCaptor.forClass(String.class);
        verify(redisTemplate).keys(patternCaptor.capture());
        assertTrue(patternCaptor.getValue().endsWith(":*"));
        verify(redisTemplate).delete(keys);
    }

    @Test
    void removeUsernameSkipsDeleteWhenNoKeys() {
        when(redisTemplate.keys(anyString())).thenReturn(Collections.<String>emptySet());

        gateway.removeUsername("admin");

        verify(redisTemplate, never()).delete(anyCollection());
    }

    @Test
    void getTokensByUsernameReturnsAllKeys() {
        Set<String> keys = new HashSet<String>(Arrays.asList("key-1", "key-2"));
        when(redisTemplate.keys(anyString())).thenReturn(keys);

        List<String> tokens = gateway.getTokensByUsername("admin");

        assertEquals(2, tokens.size());
        assertTrue(tokens.containsAll(keys));
    }

    @Test
    void removeUsernameWithPredicateDeletesMatchedToken() {
        String key = "key-1";
        when(redisTemplate.keys(anyString())).thenReturn(new HashSet<String>(Collections.singletonList(key)));
        Token token = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, 600000);
        when(valueOperations.get(key)).thenReturn(token.toJson());

        Predicate<Token> predicate = t -> "admin".equals(t.getUsername());
        gateway.removeUsername("admin", predicate);

        verify(redisTemplate).delete(key);
    }

    @Test
    void removeUsernameWithPredicateKeepsNotMatchedToken() {
        String key = "key-1";
        when(redisTemplate.keys(anyString())).thenReturn(new HashSet<String>(Collections.singletonList(key)));
        Token token = new Token("admin", null, null, Collections.singletonList("ADMIN"), 900000, 600000);
        when(valueOperations.get(key)).thenReturn(token.toJson());

        Predicate<Token> predicate = t -> "other".equals(t.getUsername());
        gateway.removeUsername("admin", predicate);

        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void removeUsernameWithPredicateSkipsMissingToken() {
        String key = "key-1";
        when(redisTemplate.keys(anyString())).thenReturn(new HashSet<String>(Collections.singletonList(key)));
        when(valueOperations.get(key)).thenReturn(null);

        gateway.removeUsername("admin", t -> true);

        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void removeUsernameWithPredicateSkipsWhenNoKeys() {
        when(redisTemplate.keys(anyString())).thenReturn(Collections.<String>emptySet());

        gateway.removeUsername("admin", t -> true);

        verify(redisTemplate, never()).delete(anyString());
        verify(valueOperations, never()).get(anyString());
    }

}
