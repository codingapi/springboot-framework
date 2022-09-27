package com.codingapi.springboot.framework.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class DES {

    public static final String KEY_ALGORITHM = "DES";

    private final Key key;

    public DES() throws NoSuchAlgorithmException {
        this(KeyGenerator.getInstance(KEY_ALGORITHM).generateKey());
    }

    public DES(Key key){
        this.key = key;
    }

    public DES(byte[] keys){
        this(new SecretKeySpec(keys, KEY_ALGORITHM));
    }

    public DES(String keys){
        this(keys.getBytes(StandardCharsets.UTF_8));
    }


    public byte[] getKey(){
        return key.getEncoded();
    }


    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }


    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }
}
