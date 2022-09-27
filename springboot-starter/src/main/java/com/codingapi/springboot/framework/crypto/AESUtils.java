package com.codingapi.springboot.framework.crypto;

import lombok.SneakyThrows;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

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
        this.aes = new AES(Base64Utils.decodeFromString(key),Base64Utils.decodeFromString(iv));
    }

    public static AESUtils getInstance() {
        return instance;
    }

    public String encode(String input) throws Exception {
        return Base64Utils.encodeToString(aes.encrypt(input.getBytes(StandardCharsets.UTF_8)));
    }

    public String decode(String input) throws Exception {
        return new String(aes.decrypt(Base64Utils.decodeFromString(input)),StandardCharsets.UTF_8);
    }

    public byte[] encode(byte[] input) throws Exception {
        return aes.encrypt(input);
    }

    public byte[] decode(byte[] input) throws Exception {
        return aes.decrypt(input);
    }
}
