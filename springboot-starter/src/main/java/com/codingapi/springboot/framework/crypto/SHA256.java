package com.codingapi.springboot.framework.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class SHA256 {

    public static String sha256(String data) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(data.getBytes(StandardCharsets.UTF_8));
            return Hex.toHexString(digest);
        } catch (NoSuchAlgorithmException exception) {
            log.error("sha256 error:", exception);
            return data;
        }
    }
}
