package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AESTest {

    static byte[] key;
    static byte[] iv;

    @BeforeAll
    static void before() throws Exception {
        AES aes = new AES();
        key = aes.getKey();
        iv = aes.getIv();

        System.out.println("keys:" + Base64Utils.encodeToString(key));
        System.out.println("ivs:" + Base64Utils.encodeToString(iv));

    }

    @Test
    void aes1() throws Exception {
        AES aes = new AES();
        String content = "hello world";
        String encrypt = Base64Utils.encodeToString(aes.encrypt(content.getBytes()));
        System.out.println("encrypt:" + encrypt);

        String decrypt = new String(aes.decrypt(Base64Utils.decodeFromString(encrypt)));
        System.out.println("decrypt:" + decrypt);

        assertEquals(content, decrypt, "AES encrypt error");
    }

    @Test
    void aes2() throws Exception {
        AES aes = new AES(key, iv);
        String content = "hello world";
        String encrypt = Base64Utils.encodeToString(aes.encrypt(content.getBytes()));
        System.out.println("encrypt:" + encrypt);
    }

}