package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AESUtilsTest {

    @Test
    void encodeDecodeToBase64() throws IOException {
       String word = "123";
       String encode = AESUtils.getInstance().encode(word);
       String decode = AESUtils.getInstance().decode(encode);
       assertEquals(word,decode,"AES encode error");
    }


    @Test
    void encodeDecode() throws IOException {
        byte[] word = "123".getBytes(StandardCharsets.UTF_8);
        byte[] encode = AESUtils.getInstance().encode(word);
        byte[] decode = AESUtils.getInstance().decode(encode);
        assertEquals(new String(word),new String(decode),"AES encode error");
    }


}