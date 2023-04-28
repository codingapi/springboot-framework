package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AESTest {

    static byte[] key;
    static byte[] iv;

    @BeforeAll
    static void before() throws Exception {
        AES aes = new AES();
        key = aes.getKey();
        iv = aes.getIv();

        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("keys:" + encoder.encodeToString(key));
        System.out.println("ivs:" + encoder.encodeToString(iv));

    }

    @Test
    void aes1() throws Exception {
        AES aes = new AES();
        String content = "hello world";
        Base64.Encoder encoder = Base64.getEncoder();
        String encrypt = encoder.encodeToString(aes.encrypt(content.getBytes()));
        System.out.println("encrypt:" + encrypt);

        Base64.Decoder decoder = Base64.getDecoder();
        String decrypt = new String(aes.decrypt(decoder.decode(encrypt)));
        System.out.println("decrypt:" + decrypt);

        assertEquals(content, decrypt, "AES encrypt error");
    }

    @Test
    void aes2() throws Exception {
        AES aes = new AES(key, iv);
        String content = "hello world";
        Base64.Encoder encoder = Base64.getEncoder();
        String encrypt = encoder.encodeToString(aes.encrypt(content.getBytes()));
        System.out.println("encrypt:" + encrypt);
    }

}