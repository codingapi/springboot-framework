package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.security.exception.TokenExpiredException;
import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenTest {

    @Autowired
    private TokenGateway tokenGateway;

    @Test
    void verify1() throws TokenExpiredException {
        String username = "admin";
        String iv = "123456";
        List<String> authorities = Collections.singletonList("ADMIN");

        Token token =tokenGateway.create(username,iv,authorities);
        token.verify();

        Token data = tokenGateway.parser(token.getToken());
        assertEquals(data.decodeIv(),iv);
        assertEquals(data.getAuthorities(),authorities);
    }

    @Test
    void verify2() throws TokenExpiredException {
        String username = "admin";
        List<String> authorities = Collections.singletonList("ADMIN");

        Token token =tokenGateway.create(username,authorities);
        token.verify();

        Token data = tokenGateway.parser(token.getToken());
        assertEquals(data.getUsername(),username);
        assertEquals(data.getAuthorities(),authorities);
    }



    @Test
    void verify3() throws TokenExpiredException {
        String username = "admin";
        TestVO testVO = new TestVO("123");
        String extra = testVO.toJson();
        List<String> authorities = Collections.singletonList("ADMIN");

        Token token =tokenGateway.create(username,authorities,extra);
        token.verify();

        Token data = tokenGateway.parser(token.getToken());
        assertEquals(data.parseExtra(TestVO.class).getName(), testVO.getName());
        assertEquals(data.getAuthorities(),authorities);
    }
}