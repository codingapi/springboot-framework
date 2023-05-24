package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class DESTest {

    @Test
    void getKey() throws NoSuchAlgorithmException {
        DES des = new DES();
        byte[] key = des.getKey();
        assertNotNull(key);
    }

    @Test
    void encryptAndDecrypt() throws Exception {
        DES des = new DES();
        String word = "123";
        byte[] encrypt = des.encrypt(word.getBytes());
        byte[] decrypt = des.decrypt(encrypt);
        assertEquals(word,new String(decrypt));
    }

}