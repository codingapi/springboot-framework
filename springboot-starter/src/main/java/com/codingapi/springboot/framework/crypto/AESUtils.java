package com.codingapi.springboot.framework.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.Security;

public class AESUtils {

    private final static AESUtils instance = new AESUtils();
    private AES aes;

    private AESUtils() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static AESUtils getInstance() {
        return instance;
    }

    void init(AES aes) {
        this.aes = aes;
    }

    public String encode(String input) throws IOException {
        return aes.encode(input);
    }

    public String decode(String input) throws IOException {
        return aes.decode(input);
    }

    public byte[] encode(byte[] input) throws IOException {
        return aes.encode(input);
    }

    public byte[] decode(byte[] input) throws IOException {
        return aes.decode(input);
    }
}
