package com.codingapi.springboot.framework.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {

    public static final String KEY_ALGORITHM = "RSA";

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    public RSA() throws NoSuchAlgorithmException  {
        Security.addProvider(new BouncyCastleProvider());
        KeyPair keyPair = generateKey();
        this.privateKey = keyPair.getPrivate();
        this.publicKey =keyPair.getPublic();
    }

    public RSA(KeyPair keyPair){
        Security.addProvider(new BouncyCastleProvider());
        this.privateKey = keyPair.getPrivate();
        this.publicKey =keyPair.getPublic();
    }

    public RSA(PrivateKey privateKey, PublicKey publicKey){
        Security.addProvider(new BouncyCastleProvider());
        this.privateKey = privateKey;
        this.publicKey =publicKey;
    }

    public RSA(byte[] privateKey, byte[] publicKey) throws Exception {
        this.privateKey = this.getPrivateKeyFromString(privateKey);
        this.publicKey = this.getPublicKeyFromString(publicKey);
    }


    /**
     * Generate key which contains a pair of privae and public key using 2048 bytes
     *
     * @return key pair
     * @throws NoSuchAlgorithmException
     */
    public KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }


    /**
     * Generates Private Key from byte[] string
     *
     * @param key byte[] key
     * @return The PrivateKey
     * @throws Exception
     */
    private  PrivateKey getPrivateKeyFromString(byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key);
        return keyFactory.generatePrivate(privateKeySpec);
    }


    /**
     * Generates Public Key from byte[] string
     *
     * @param key byte[] key
     * @return The PublicKey
     * @throws NoSuchAlgorithmException,InvalidKeySpecException
     */
    private  PublicKey getPublicKeyFromString(byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
        return keyFactory.generatePublic(publicKeySpec);
    }


    public byte[] getPrivateKey() {
        return privateKey.getEncoded();
    }

    public byte[]  getPublicKey() {
        return publicKey.getEncoded();
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



}
