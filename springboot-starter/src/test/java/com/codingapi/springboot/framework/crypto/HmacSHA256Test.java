package com.codingapi.springboot.framework.crypto;

import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HmacSHA256Test {

    @Test
    void sha256() throws NoSuchAlgorithmException, InvalidKeyException {
       String data = Base64Utils.encodeToString(HmacSHA256.sha256("123456".getBytes(),"123456".getBytes()));
       assertEquals("uK0Io6VH41gpuCG3U3AwHdjEsGvdd3H5tUGnWRQGhxg=",data);
    }
}