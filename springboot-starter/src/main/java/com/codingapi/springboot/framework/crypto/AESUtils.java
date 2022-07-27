package com.codingapi.springboot.framework.crypto;

import java.io.IOException;

public class AESUtils {

    private AESUtils(){

    }

    private final static AESUtils instance = new AESUtils();

    public static AESUtils getInstance() {
        return instance;
    }


    private AES aes;

    void init(AES aes){
        this.aes = aes;
    }

    public String encodeToBase64(String input) throws IOException {
        return aes.encodeToBase64(input);
    }

    public String decodeToBase64(String input) throws IOException {
        return aes.decodeToBase64(input);
    }

    public byte[] encode(byte[] input) throws IOException {
        return aes.encode(input);
    }

    public byte[] decode(byte[] input) throws IOException {
        return aes.decode(input);
    }
}
