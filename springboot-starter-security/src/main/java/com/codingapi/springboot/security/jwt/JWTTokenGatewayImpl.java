package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;

import java.util.List;

public class JWTTokenGatewayImpl implements TokenGateway {

    private final Jwt jwt;

    public JWTTokenGatewayImpl(SecurityJWTProperties properties) {
        this.jwt = new Jwt(properties.getSecretKey(), properties.getJwtTime(), properties.getJwtRestTime());
    }

    @Override
    public Token create(String username, String password, List<String> authorities, String extra) {
        return jwt.create(username, password, authorities, extra);
    }

    @Override
    public Token parser(String sign) {
        return jwt.parser(sign);
    }
}
