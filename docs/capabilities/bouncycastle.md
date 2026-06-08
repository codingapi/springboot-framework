---
name: bouncycastle
description: 密码学算法库，提供 AES/RSA 等加密算法支持
status: 已实现
scope: 后端
source: 框架:BouncyCastle
---

## 解决什么问题

框架需要提供数据加解密能力，用于：
- 安全模块中 Token IV（初始化向量）的 AES 加密/解密
- RSA 非对称加密用于数据传输安全
- 作为 JCA/JCE Provider 注册，增强 JDK 默认的密码学算法支持

BouncyCastle 作为 Java 密码学扩展提供者（Provider），通过 `Security.addProvider()` 注册后，JDK 标准 Cipher API 即可使用更丰富的算法实现。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明版本 1.79 -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk18on</artifactId>
    <version>1.79</version>
</dependency>
```

### 核心类

- `AES` — AES 对称加密封装类（支持 CBC/PKCS5Padding）
- `RSA` — RSA 非对称加密封装类（2048 位密钥对）
- `AESUtils` — AES 工具类（单例模式）
- `RSAUtils` — RSA 工具类（单例模式，内置默认密钥对）

## 使用实例

### 1. AES 对称加密

```java
// AES.java - 使用 BouncyCastle Provider
public class AES {
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public AES() throws Exception {
        this(CIPHER_ALGORITHM, 256);
    }

    public AES(String algorithm, int generateKeySize) throws Exception {
        // 注册 BouncyCastle Provider
        Security.addProvider(new BouncyCastleProvider());
        this.algorithm = algorithm;
        this.key = generateKey(generateKeySize);
        this.iv = generateIV(randomIv());
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
```

### 2. RSA 非对称加密

```java
// RSA.java - 使用 BouncyCastle Provider
public class RSA {
    public RSA() throws NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleProvider());
        KeyPair keyPair = generateKey();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public byte[] encrypt(byte[] text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(text);
    }

    public byte[] decrypt(byte[] text) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(text);
    }
}
```

### 3. Security 模块中的 AES 工具

```java
// AESTools.java - Security 模块中使用 AES 加密 Token IV
public class AESTools {
    private AES aes;

    public void init(AES aes) {
        this.aes = aes;
    }

    // Token 创建时加密 IV
    // Token.java: this.iv = AESTools.getInstance().encode(iv);
    // Token 解析时解密 IV
    // Token.java: return AESTools.getInstance().decode(iv);
}
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter | `AES`、`RSA`、`AESUtils`、`RSAUtils` 加密工具类 |
| springboot-starter-security | `AESTools` Token IV 加解密 |
