package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.framework.crypto.AES;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class TokenTest {

    @SneakyThrows
    @Test
    void aesTest() {
        AES aes = new AES("1234567890123456", "1234567890123456");
        final String input = "hello world!";
        String encode = aes.encodeToBase64(input);
        System.out.println(encode);
        String decode = aes.decodeToBase64(encode);
        System.out.println(decode);
    }


}