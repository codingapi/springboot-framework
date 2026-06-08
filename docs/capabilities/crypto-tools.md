---
name: crypto-tools
description: 密码学工具套件，AES（CBC 模式）/RSA（BouncyCastle）/DES 加解密 + SHA256/HmacSHA256 哈希签名，提供实例化工具类和静态方法两种 API
status: 已实现
scope: 后端
source: 项目自有
last_commit: 36eae41d
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/AES.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/AESUtils.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/DES.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/DESUtils.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/RSA.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/RSAUtils.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/SHA256.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/crypto/HmacSHA256.java
  - springboot-starter-security/src/main/java/com/codingapi/springboot/security/crypto/AESTools.java
---

## 解决什么问题

企业应用中大量场景需要密码学支持：敏感数据存储加密、API 通信签名验证、Token 加解密、文件完整性校验等。直接使用 JDK 原生 `javax.crypto` / `java.security` API 存在以下问题：

- **API 繁琐**：每次加解密都需要手动创建 Cipher、KeySpec、AlgorithmParameters，代码冗长且容易出错
- **Provider 管理**：BouncyCastle 等第三方 Provider 需要手动注册，遗漏会导致运行时异常
- **密钥格式混乱**：byte[]、String、Base64 之间的转换散落在各处
- **缺少统一入口**：不同算法的使用方式不一致，增加学习成本

本能力封装了五种常用密码学算法，提供两种使用风格：

- **实例化 API**（`AES`/`DES`/`RSA`）：适合自定义密钥、动态生成密钥对的场景
- **单例工具类**（`AESUtils`/`DESUtils`/`RSAUtils`/`AESTools`）：内置预置密钥，开箱即用，适合应用内统一加解密
- **静态方法**（`SHA256`/`HmacSHA256`）：无状态哈希/签名，直接调用

## 如何使用

### AES 加解密（CBC/PKCS5Padding）

基于 BouncyCastle Provider，默认使用 `AES/CBC/PKCS5Padding` 算法。

```java
// 自动生成 256 位密钥 + 随机 IV
AES aes = new AES();

// 指定密钥长度
AES aes = new AES(128);

// 使用指定密钥和 IV（byte[]）
AES aes = new AES(keyBytes, ivBytes);

// 使用字符串密钥和 IV
AES aes = new AES("mySecretKey12345", "myIvParam1234567");

// 加解密
byte[] encrypted = aes.encrypt("Hello".getBytes(StandardCharsets.UTF_8));
byte[] decrypted = aes.decrypt(encrypted);

// 获取密钥和 IV（用于传输或存储）
byte[] key = aes.getKey();
byte[] iv = aes.getIv();
```

#### AESUtils — 预置密钥单例

内置 Base64 编码的预置密钥和 IV，适用于应用内部数据加密：

```java
AESUtils utils = AESUtils.getInstance();

// 字符串加解密（自动 Base64 编解码）
String cipher = utils.encode("敏感数据");
String plain = utils.decode(cipher);

// byte[] 加解密（原始字节，不做 Base64）
byte[] encrypted = utils.encode(rawBytes);
byte[] decrypted = utils.decode(encrypted);
```

#### AESTools — Security 模块的 AES 封装

位于 `springboot-starter-security` 模块，通过 `init(AES)` 方法注入自定义 AES 实例，适合安全模块统一管理密钥：

```java
// 由安全模块配置类初始化
AESTools.getInstance().init(new AES(customKey, customIv));

// 使用方式与 AESUtils 一致
String cipher = AESTools.getInstance().encode("data");
String plain = AESTools.getInstance().decode(cipher);
```

### DES 加解密

标准 DES 算法，ECB 模式。

```java
// 自动生成密钥
DES des = new DES();

// 使用指定密钥
DES des = new DES("myKey123");
DES des = new DES(keyBytes);

// 加解密
byte[] encrypted = des.encrypt(data);
byte[] decrypted = des.decrypt(encrypted);
```

#### DESUtils — 预置密钥单例

```java
DESUtils utils = DESUtils.getInstance();
String cipher = utils.encode("明文");
String plain = utils.decode(cipher);
```

### RSA 加解密（2048 位）

基于 BouncyCastle Provider，使用 `RSA/ECB/PKCS1Padding`，密钥长度 2048 位。

```java
// 自动生成密钥对
RSA rsa = new RSA();

// 从已有密钥构造
RSA rsa = new RSA(privateKeyBytes, publicKeyBytes);
RSA rsa = new RSA(publicKeyBytes);  // 仅公钥（只能加密）
RSA rsa = new RSA(keyPair);

// 公钥加密 / 私钥解密
byte[] encrypted = rsa.encrypt("Hello".getBytes(StandardCharsets.UTF_8));
byte[] decrypted = rsa.decrypt(encrypted);

// 导出密钥
byte[] pubKey = rsa.getPublicKey();
byte[] priKey = rsa.getPrivateKey();
```

#### RSAUtils — 预置密钥单例

内置 Base64 编码的 2048 位 RSA 密钥对：

```java
RSAUtils utils = RSAUtils.getInstance();
String cipher = utils.encode("敏感数据");
String plain = utils.decode(cipher);
```

### SHA256 哈希

无状态静态方法，返回十六进制小写字符串：

```java
String hash = SHA256.sha256("Hello World");
// 输出: "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e"
```

### HmacSHA256 签名

无状态静态方法，返回 HMAC-SHA256 签名字节数组：

```java
byte[] signature = HmacSHA256.sha256(
    "message".getBytes(StandardCharsets.UTF_8),
    "secret-key".getBytes(StandardCharsets.UTF_8)
);
String hexSign = HexFormat.of().formatHex(signature);
```

## 使用实例

### 1. API 请求签名验证

```java
// 客户端签名
String timestamp = String.valueOf(System.currentTimeMillis());
String payload = userId + timestamp;
byte[] sign = HmacSHA256.sha256(
    payload.getBytes(StandardCharsets.UTF_8),
    appSecret.getBytes(StandardCharsets.UTF_8)
);
String signature = HexFormat.of().formatHex(sign);

// 服务端验签
byte[] expected = HmacSHA256.sha256(
    payload.getBytes(StandardCharsets.UTF_8),
    appSecret.getBytes(StandardCharsets.UTF_8)
);
boolean valid = Arrays.equals(sign, expected);
```

### 2. 用户敏感字段存储加密

```java
// 入库时加密
String encryptedPhone = AESUtils.getInstance().encode(user.getPhone());
user.setPhone(encryptedPhone);
userRepository.save(user);

// 查询时解密
String phone = AESUtils.getInstance().decode(user.getPhone());
```

### 3. RSA 密钥交换 + AES 数据加密

```java
// 发送方：用接收方公钥加密 AES 密钥
RSA rsa = new RSA(receiverPublicKeyBytes);
AES aes = new AES();
byte[] encryptedAesKey = rsa.encrypt(aes.getKey());

// 用 AES 加密实际数据
byte[] encryptedData = aes.encrypt(sensitiveData);

// 接收方：用私钥解密 AES 密钥，再解密数据
RSA rsaReceiver = new RSA(receiverPrivateKeyBytes, receiverPublicKeyBytes);
byte[] aesKey = rsaReceiver.decrypt(encryptedAesKey);
AES aesDecrypt = new AES(aesKey, aes.getIv());
byte[] plainData = aesDecrypt.decrypt(encryptedData);
```

### 4. 密码哈希存储

```java
// 注册时
String passwordHash = SHA256.sha256(rawPassword + salt);
user.setPassword(passwordHash);

// 登录验证
String inputHash = SHA256.sha256(inputPassword + salt);
boolean match = passwordHash.equals(inputHash);
```

### 5. 安全模块中自定义 AES 密钥

```java
@Configuration
public class SecurityCryptoConfig {

    @PostConstruct
    public void initAESTools() throws Exception {
        String key = environment.getProperty("security.aes.key");
        String iv = environment.getProperty("security.aes.iv");
        AESTools.getInstance().init(new AES(key, iv));
    }
}
```
