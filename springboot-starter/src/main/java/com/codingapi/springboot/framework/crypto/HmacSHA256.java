package com.codingapi.springboot.framework.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacSHA256 {

    public static byte[] sha256(byte[] message,byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSha256");
        SecretKeySpec secretKey = new SecretKeySpec(secret,"HmacSha256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(message);
    }
}
