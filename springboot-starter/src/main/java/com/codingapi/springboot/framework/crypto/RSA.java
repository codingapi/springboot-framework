package com.codingapi.springboot.framework.crypto;

import lombok.Getter;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class RSA {

    @Getter
    private final PrivateKey privateKey;

    @Getter
    private final PublicKey publicKey;

    public RSA() throws NoSuchAlgorithmException {
        KeyPair keyPair =  RSAUtils.generateKey();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public RSA(PrivateKey privateKey, PublicKey publicKey){
        this.privateKey = privateKey;
        this.publicKey =publicKey;
    }

    public RSA(String privateKey, String publicKey) throws Exception {
        this.privateKey = RSAUtils.getPrivateKeyFromString(privateKey);
        this.publicKey = RSAUtils.getPublicKeyFromString(publicKey);
    }


    /**
     * Encrypt a text using public key.
     *
     * @param text The original unencrypted text
     * @return Encrypted text
     * @throws Exception
     */
    public byte[] encrypt(byte[] text) throws Exception {
        byte[] cipherText = null;
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        cipherText = cipher.doFinal(text);
        return cipherText;
    }

    /**
     * Encrypt a text using public key. The result is enctypted BASE64 encoded text
     *
     * @param text The original unencrypted text
     * @return Encrypted text encoded as BASE64
     * @throws Exception
     */
    public String encrypt(String text) throws Exception {
        String encryptedText;
        byte[] cipherText = encrypt(text.getBytes(StandardCharsets.UTF_8));
        encryptedText = Base64.toBase64String(cipherText);
        return encryptedText;
    }


    /**
     * Decrypt text using private key
     *
     * @param text The encrypted text
     * @return The unencrypted text
     * @throws Exception
     */
    public byte[] decrypt(byte[] text) throws Exception {
        byte[] dectyptedText = null;
        // decrypt the text using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        dectyptedText = cipher.doFinal(text);
        return dectyptedText;

    }

    /**
     * Decrypt BASE64 encoded text using private key
     *
     * @param text The encrypted text, encoded as BASE64
     * @return The unencrypted text encoded as UTF8
     * @throws Exception
     */
    public String decrypt(String text) throws Exception {
        String result;
        // decrypt the text using the private key
        byte[] dectyptedText = decrypt(Base64.decode(text));
        result = new String(dectyptedText, StandardCharsets.UTF_8);
        return result;
    }

}
