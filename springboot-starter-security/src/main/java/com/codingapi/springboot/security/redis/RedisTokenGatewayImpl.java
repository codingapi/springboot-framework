package com.codingapi.springboot.security.redis;

import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;

import java.util.List;

public class RedisTokenGatewayImpl implements TokenGateway {

    private final RedisTokenGateway redisTokenGateway;

    public RedisTokenGatewayImpl(RedisTokenGateway redisTokenGateway) {
        this.redisTokenGateway = redisTokenGateway;
    }

    @Override
    public Token create(String username, String iv, List<String> authorities, String extra) {
        return redisTokenGateway.create(username, iv, authorities, extra);
    }

    @Override
    public Token parser(String sign) {
        return redisTokenGateway.getToken(sign);
    }

}
