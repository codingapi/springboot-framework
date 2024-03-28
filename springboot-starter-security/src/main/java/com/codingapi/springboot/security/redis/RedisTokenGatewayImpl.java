package com.codingapi.springboot.security.redis;

import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.UUID;

public class RedisTokenGatewayImpl implements TokenGateway {

    private final RedisTemplate<String, Token> redisTemplate;
    private final int validTime;
    private final int restTime;

    public RedisTokenGatewayImpl(RedisTemplate<String, Token> redisTemplate, SecurityRedisProperties properties) {
        this.redisTemplate = redisTemplate;
        this.validTime = properties.getValidTime();
        this.restTime = properties.getRestTime();
    }

    @Override
    public Token create(String username, String iv, List<String> authorities, String extra) {
        Token token = new Token(username, iv, extra, authorities, validTime, restTime);
        String key = String.format("%s:%s", username, UUID.randomUUID().toString().replaceAll("-", ""));
        token.setToken(key);
        redisTemplate.opsForValue().set(key, token);
        return token;
    }

    @Override
    public Token parser(String sign) {
        return redisTemplate.opsForValue().get(sign);
    }

}
