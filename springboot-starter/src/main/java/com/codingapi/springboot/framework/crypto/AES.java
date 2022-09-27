package com.codingapi.springboot.framework.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.Random;

public class AES {

    public static final String KEY_ALGORITHM = "AES";

    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private final Key key;
    private final AlgorithmParameters iv;
    private final String algorithm;

    public AES(String algorithm,Key key,AlgorithmParameters iv){
        Security.addProvider(new BouncyCastleProvider());
        this.algorithm = algorithm;
        this.key = key;
        this.iv = iv;
    }

    public AES(String algorithm, int generateKeySize) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        this.algorithm = algorithm;
        this.key = generateKey(generateKeySize);
        this.iv = generateIV(randomIv());
    }

    public AES(String algorithm, byte[] keys, byte[] ivs) throws Exception{
        Security.addProvider(new BouncyCastleProvider());
        this.algorithm = algorithm;
        this.key = convertToKey(keys);
        this.iv = generateIV(ivs);
    }

    public AES() throws Exception {
        this(CIPHER_ALGORITHM,256);
    }


    public AES(int generateKeySize) throws Exception {
        this(CIPHER_ALGORITHM,generateKeySize);
    }

    public AES(byte[] keys, byte[] ivs) throws Exception{
        this(CIPHER_ALGORITHM,keys,ivs);
    }

    public AES(String key, String iv) throws Exception{
        this(CIPHER_ALGORITHM,key.getBytes(StandardCharsets.UTF_8),iv.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] randomIv(){
        Random random = new Random();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return  bytes;
    }

    private SecretKey generateKey(int keySize) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }


    private AlgorithmParameters generateIV(byte[] ivs) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
        params.init(new IvParameterSpec(ivs));
        return params;
    }

    private Key convertToKey(byte[] keyBytes){
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public byte[] getKey(){
        return key.getEncoded();
    }

    public byte[] getIv() throws IOException {
        return iv.getEncoded();
    }

    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }


    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encryptedData);
    }

}
