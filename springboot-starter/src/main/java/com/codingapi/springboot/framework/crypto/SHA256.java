package com.codingapi.springboot.framework.crypto;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
public class SHA256 {

    public static String sha256(String data) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            log.error("sha256 error:", exception);
            return data;
        }
    }
}
