package com.codingapi.springboot.framework.crypto;

import org.apache.commons.crypto.stream.CryptoInputStream;
import org.apache.commons.crypto.stream.CryptoOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AES {

    private final SecretKeySpec key;
    private final IvParameterSpec iv;
    private final Properties properties;
    private final String transform;

    public AES(String transform, byte[] keys, byte[] iv) {
        this.key = new SecretKeySpec(keys, "AES");
        this.iv = new IvParameterSpec(iv);
        this.properties = new Properties();
        this.transform = transform;
        AESUtils.getInstance().init(this);
    }


    public AES(String key, String iv) {
        this("AES/CBC/PKCS5Padding", key.getBytes(StandardCharsets.UTF_8), iv.getBytes(StandardCharsets.UTF_8));
    }


    public String encodeToBase64(String input) throws IOException {
        return Base64Utils.encodeToString(encode(input.getBytes(StandardCharsets.UTF_8)));
    }

    public String decodeToBase64(String input) throws IOException {
        return new String(decode(Base64Utils.decodeFromString(input)), StandardCharsets.UTF_8);
    }


    public byte[] encode(byte[] input) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CryptoOutputStream cos = new CryptoOutputStream(transform, properties, outputStream, key, iv)) {
            cos.write(input);
            cos.flush();
        }
        return outputStream.toByteArray();
    }

    public byte[] decode(byte[] input) throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(input);
        try (CryptoInputStream cis = new CryptoInputStream(transform, properties, inputStream, key, iv)) {
            return IOUtils.toByteArray(cis);
        }
    }

}
