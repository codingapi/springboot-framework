package com.codingapi.springboot.security.redis;

import com.alibaba.fastjson2.JSONObject;
import com.codingapi.springboot.security.gateway.Token;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class RedisTokenGateway {

    private final RedisTemplate<String, String> redisTemplate;
    private final int validTime;
    private final int restTime;

    public RedisTokenGateway(RedisTemplate<String, String> redisTemplate, SecurityRedisProperties properties) {
        this.redisTemplate = redisTemplate;
        this.validTime = properties.getValidTime();
        this.restTime = properties.getRestTime();
    }

    public Token create(String username, String iv, List<String> authorities, String extra) {
        Token token = new Token(username, iv, extra, authorities, validTime, restTime);
        String key = String.format("%s:%s", username, UUID.randomUUID().toString().replaceAll("-", ""));
        token.setToken(key);
        redisTemplate.opsForValue().set(key, token.toJson(), validTime, TimeUnit.MILLISECONDS);
        return token;
    }

    public Token parser(String sign) {
        String json = redisTemplate.opsForValue().get(sign);
        if (json == null) {
            return null;
        }
        return JSONObject.parseObject(json, Token.class);
    }

    /**
     * 删除token
     * @param token token
     */
    public void removeToken(String token) {
        redisTemplate.delete(token);
    }

    /**
     * 重置token
     * @param token token
     */
    public void resetToken(Token token){
        redisTemplate.opsForValue().set(token.getToken(), token.toJson(), validTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 删除用户
     * @param username 用户名
     */
    public void removeUsername(String username) {
        Set<String> keys = redisTemplate.keys(username + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 自定义删除用户
     * @param username 用户名
     * @param predicate 条件
     */
    public void removeUsername(String username, Predicate<Token> predicate) {
        Set<String> keys = redisTemplate.keys(username + ":*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Token token = parser(key);
                if (token != null && predicate.test(token)) {
                    redisTemplate.delete(key);
                }
            }
        }
    }


}
