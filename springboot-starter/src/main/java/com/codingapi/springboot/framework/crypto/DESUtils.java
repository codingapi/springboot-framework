package com.codingapi.springboot.framework.crypto;

import lombok.SneakyThrows;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

public class DESUtils {

    /**
     * base64 key
     */
    public final static String key = "QVMjQCRYU2Q=";

    private final static DESUtils instance = new DESUtils();

    private final DES des;

    @SneakyThrows
    private DESUtils() {
        this.des = new DES(Base64Utils.decodeFromString(key));
    }

    public static DESUtils getInstance() {
        return instance;
    }

    public String encode(String input) throws Exception {
        return Base64Utils.encodeToString(des.encrypt(input.getBytes(StandardCharsets.UTF_8)));
    }

    public String decode(String input) throws Exception {
        return new String(des.decrypt(Base64Utils.decodeFromString(input)),StandardCharsets.UTF_8);
    }

    public byte[] encode(byte[] input) throws Exception {
        return des.encrypt(input);
    }

    public byte[] decode(byte[] input) throws Exception {
        return des.decrypt(input);
    }

}
