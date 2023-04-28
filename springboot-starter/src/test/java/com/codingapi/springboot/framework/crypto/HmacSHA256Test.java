package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HmacSHA256Test {

    @Test
    void sha256() throws NoSuchAlgorithmException, InvalidKeyException {
       String data = Base64.getEncoder().encodeToString(HmacSHA256.sha256("123456".getBytes(),"123456".getBytes()));
       assertEquals("uK0Io6VH41gpuCG3U3AwHdjEsGvdd3H5tUGnWRQGhxg=",data);
    }
}