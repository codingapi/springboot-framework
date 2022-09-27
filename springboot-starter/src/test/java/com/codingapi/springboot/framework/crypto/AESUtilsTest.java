package com.codingapi.springboot.framework.crypto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class AESUtilsTest {

    @Test
    void encodeDecodeToBase64() throws Exception {
       String word = "123";
       String encode = AESUtils.getInstance().encode(word);
       log.info("encode:{}",encode);
       String decode = AESUtils.getInstance().decode(encode);
       assertEquals(word,decode,"AES encode error");
    }


    @Test
    void encodeDecode() throws Exception {
        byte[] word = "123".getBytes(StandardCharsets.UTF_8);
        byte[] encode = AESUtils.getInstance().encode(word);
        byte[] decode = AESUtils.getInstance().decode(encode);
        assertEquals(new String(word),new String(decode),"AES encode error");
    }


}