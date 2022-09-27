package com.codingapi.springboot.security.crypto;

import com.codingapi.springboot.framework.crypto.AES;
import lombok.SneakyThrows;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

public class MyAES {

    private final static MyAES instance = new MyAES();

    private AES aes;

    private MyAES() {
    }

    void init(AES aes) {
        this.aes = aes;
    }

    public static MyAES getInstance() {
        return instance;
    }

    @SneakyThrows
    public String encode(String input)  {
        return Base64Utils.encodeToString(aes.encrypt(input.getBytes(StandardCharsets.UTF_8)));
    }

    @SneakyThrows
    public String decode(String input)  {
        return new String(aes.decrypt(Base64Utils.decodeFromString(input)),StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public byte[] encode(byte[] input)  {
        return aes.encrypt(input);
    }

    @SneakyThrows
    public byte[] decode(byte[] input){
        return aes.decrypt(input);
    }
}
