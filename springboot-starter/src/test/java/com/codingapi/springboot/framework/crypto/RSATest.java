package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class RSATest {

    @Test
    void getPrivateKey()  throws NoSuchAlgorithmException {
        RSA rsa = new RSA();
        assertNotNull(rsa.getPrivateKey());
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("privateKey:" + encoder.encodeToString(rsa.getPrivateKey()));
    }

    @Test
    void getPublicKey() throws NoSuchAlgorithmException {
        RSA rsa = new RSA();
        assertNotNull(rsa.getPublicKey());
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("publicKey:" + encoder.encodeToString(rsa.getPublicKey()));
    }

    @Test
    void encryptAndDecrypt() throws Exception {
        RSA rsa = new RSA();
        String content = "hello world";
        byte[] encrypt = rsa.encrypt(content.getBytes());
        byte[] data = rsa.decrypt(encrypt);
        assertEquals(content,new String(data));
    }

}