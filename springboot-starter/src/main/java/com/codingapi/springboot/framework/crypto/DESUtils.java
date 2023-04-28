package com.codingapi.springboot.framework.crypto;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DESUtils {

    /**
     * base64 key
     */
    public final static String key = "QVMjQCRYU2Q=";

    private final static DESUtils instance = new DESUtils();

    private final DES des;

    @SneakyThrows
    private DESUtils() {
        this.des = new DES(Base64.getDecoder().decode(key));
    }

    public static DESUtils getInstance() {
        return instance;
    }

    public String encode(String input) throws Exception {
        return Base64.getEncoder().encodeToString(des.encrypt(input.getBytes(StandardCharsets.UTF_8)));
    }


    public String decode(String input) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(des.decrypt(decoder.decode(input)),StandardCharsets.UTF_8);
    }

    public byte[] encode(byte[] input) throws Exception {
        return des.encrypt(input);
    }

    public byte[] decode(byte[] input) throws Exception {
        return des.decrypt(input);
    }

}
