package com.codingapi.springboot.security.jwt;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.exception.LocaleMessageException;
import com.codingapi.springboot.security.gateway.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtTokenGateway {

    private final SecretKey key;
    private final int validTime;
    private final int restTime;

    public JwtTokenGateway(SecurityJWTProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.validTime = properties.getValidTime();
        this.restTime = properties.getRestTime();
    }

    public Token create(String username, List<String> authorities, String extra){
        return create(username, null,authorities, extra);
    }

    public Token create(String username, List<String> authorities){
        return create(username, null,authorities, null);
    }

    public Token create(String username, String iv, List<String> authorities){
        return create(username, iv,authorities, null);
    }

    public Token create(String username, String iv,List<String> authorities,String extra){
        Token token = new Token(username, iv,extra, authorities, validTime, restTime);
        String jwt = Jwts.builder().subject(token.toJson()).signWith(key).compact();
        token.setToken(jwt);
        return token;
    }

    public Token parser(String sign) {
        try {
            Jws<Claims> jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(sign);
            if (jws != null) {
                String subject = jws.getPayload().getSubject();
                return JSONObject.parseObject(subject, Token.class);
            }
            throw new LocaleMessageException("token.error", "token失效,请重新登录.");
        } catch (Exception exp) {
            throw new LocaleMessageException("token.error", exp.getMessage());
        }
    }
}
