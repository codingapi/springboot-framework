---
name: commons-crypto
description: Apache 加密工具封装，提供 AES 等加密操作
status: 已实现
scope: 后端
source: 框架:Apache Commons Crypto
---

## 解决什么问题

框架在 springboot-starter 模块中提供了统一的加密工具抽象，用于：
- AES 对称加密的便捷封装
- 安全模块中 Token IV 向量的加解密
- 为上层业务提供简洁的加密/解密 API

项目中通过 `AES`、`AESUtils`、`AESTools` 等类对加密操作进行了封装，结合 BouncyCastle Provider 提供完整的加密能力。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明版本 1.2.0 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-crypto</artifactId>
    <version>1.2.0</version>
</dependency>
```

### 核心类

- `AES` — AES 加密封装（位于 `com.codingapi.springboot.framework.crypto`）
- `AESUtils` — AES 单例工具类
- `AESTools` — Security 模块的 AES 工具（自动注入配置密钥）

## 使用实例

### 1. AESUtils 单例使用

```java
// AESUtils.java - 框架级 AES 工具
public class AESUtils {
    private static final AESUtils instance = new AESUtils();
    private final AES aes;

    private AESUtils() {
        // 初始化默认 AES 实例
        this.aes = new AES();
    }

    public static AESUtils getInstance() {
        return instance;
    }

    // 提供 encode/decode 方法供业务调用
}
```

### 2. Security 模块 AESTools

```java
// AESTools.java - 安全模块中的 AES 工具
@Component
public class AESTools {
    private static AESTools instance;
    private AES aes;

    @PostConstruct
    public void init(AES aes) {
        this.aes = aes;
        instance = this;
    }

    public static AESTools getInstance() {
        return instance;
    }

    // 在 Token 中使用:
    // 加密: AESTools.getInstance().encode(iv)
    // 解密: AESTools.getInstance().decode(iv)
}
```

### 3. Security 加密配置

```java
// SecurityCryptoConfiguration.java
@Configuration
public class SecurityCryptoConfiguration {

    @Bean
    public AES aes(CodingApiSecurityProperties properties) {
        // 根据配置创建 AES 实例
        return new AES(properties.getAesKey(), properties.getAesIv());
    }

    @Bean
    public AESTools aesTools() {
        return new AESTools();
    }
}
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter | `AES`、`AESUtils` 基础加密工具 |
| springboot-starter-security | `AESTools`、`SecurityCryptoConfiguration` 安全加密配置 |
