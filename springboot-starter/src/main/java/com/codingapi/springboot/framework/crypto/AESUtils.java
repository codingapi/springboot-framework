package com.codingapi.springboot.framework.crypto;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtils {

    /**
     * base64 key
     */
    public final static String key = "QUNEWCQlXiYqJCNYQ1phc0FDRFgkJV4mKiQjWENaYXM=";

    /**
     * base64 iv
     */
    public final static String iv = "QUNYRkdIQEVEUyNYQ1phcw==";


    private final static AESUtils instance = new AESUtils();

    private final AES aes;

    @SneakyThrows
    private AESUtils() {
        this.aes = new AES(Base64.getDecoder().decode(key),Base64.getDecoder().decode(iv));
    }

    public static AESUtils getInstance() {
        return instance;
    }

    public String encode(String input) throws Exception {
        return Base64.getEncoder().encodeToString(aes.encrypt(input.getBytes(StandardCharsets.UTF_8)));
    }

    public String decode(String input) throws Exception {
        return new String(aes.decrypt(Base64.getDecoder().decode(input)),StandardCharsets.UTF_8);
    }

    public byte[] encode(byte[] input) throws Exception {
        return aes.encrypt(input);
    }

    public byte[] decode(byte[] input) throws Exception {
        return aes.decrypt(input);
    }
}
