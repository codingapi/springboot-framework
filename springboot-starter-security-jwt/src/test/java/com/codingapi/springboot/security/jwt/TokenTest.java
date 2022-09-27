package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.security.exception.TokenExpiredException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenTest {

    @Autowired
    private Jwt jwt;

    @Test
    void verify() throws TokenExpiredException {
        String username = "admin";
        String iv = "123456";
        List<String> authorities = Collections.singletonList("ADMIN");

        Token token =jwt.create(username,iv,authorities);
        token.verify();

        Token data = jwt.parser(token.getToken());
        assertEquals(data.getDecodeIv(),iv);
    }
}