package com.codingapi.springboot.framework.crypto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class RSAUtilsTest {

    @Test
    void encode() throws Exception {
        String word = "123";
        String encode = RSAUtils.getInstance().encode(word);
        log.info("encode:{}",encode);
        String decode = RSAUtils.getInstance().decode(encode);
        assertEquals(word,decode,"AES encode error");
    }

    @Test
    void decode() throws Exception {
        byte[] word = "123".getBytes(StandardCharsets.UTF_8);
        byte[] encode = RSAUtils.getInstance().encode(word);
        byte[] decode = RSAUtils.getInstance().decode(encode);
        assertEquals(new String(word),new String(decode),"AES encode error");
    }
}