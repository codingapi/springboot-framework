package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;

import java.util.List;

public class JWTTokenGatewayImpl implements TokenGateway {

    private final JwtTokenGateway jwtTokenGateway;

    public JWTTokenGatewayImpl(JwtTokenGateway jwtTokenGateway) {
        this.jwtTokenGateway = jwtTokenGateway;
    }

    @Override
    public Token create(String username, String password, List<String> authorities, String extra) {
        return jwtTokenGateway.create(username, password, authorities, extra);
    }

    @Override
    public Token parser(String sign) {
        return jwtTokenGateway.parser(sign);
    }
}
