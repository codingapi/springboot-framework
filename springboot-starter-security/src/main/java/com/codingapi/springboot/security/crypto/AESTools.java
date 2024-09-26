package com.codingapi.springboot.security.crypto;

import com.codingapi.springboot.framework.crypto.AES;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESTools {

    @Getter
    private final static AESTools instance = new AESTools();

    private AES aes;

    private AESTools() {
    }

    void init(AES aes) {
        this.aes = aes;
    }

    @SneakyThrows
    public String encode(String input)  {
        return Base64.getEncoder().encodeToString(aes.encrypt(input.getBytes(StandardCharsets.UTF_8)));
    }

    @SneakyThrows
    public String decode(String input)  {
        return new String(aes.decrypt(Base64.getDecoder().decode(input)),StandardCharsets.UTF_8);
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
