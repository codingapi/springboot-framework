package com.codingapi.springboot.security.jwt;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.exception.LocaleMessageException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

public class Jwt {

    private final Key key;
    private final int jwtTime;
    private final int jwtRestTime;

    public Jwt(String secretKey, int jwtTime, int jwtRestTime) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.jwtTime = jwtTime;
        this.jwtRestTime = jwtRestTime;
    }

    public Token create(String username,String password,List<String> authorities) throws IOException {
        Token token = new Token(username,password,authorities,jwtTime,jwtRestTime);
        String jwt = Jwts.builder().setSubject(token.toJson()).signWith(key).compact();
        token.setToken(jwt);
        return token;
    }

    public Token parser(String sign){
        try {
            Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(sign);
            if (jws != null) {
                String subject = jws.getBody().getSubject();
                return JSONObject.parseObject(subject, Token.class);
            }
            throw new LocaleMessageException("token.error","token失效,请重新登录.");
        }catch (Exception exp){
            throw new LocaleMessageException("token.error",exp.getMessage());
        }
    }
}
