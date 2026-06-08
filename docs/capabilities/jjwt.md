---
name: jjwt
description: JWT 令牌生成与解析库
status: 已实现
scope: 后端
source: 框架:JJWT
---

## 解决什么问题

在安全认证模块中，需要实现无状态的 JWT（JSON Web Token）认证机制：
- 用户登录后生成签名的 JWT Token
- 后续请求中验证 Token 签名和有效期
- 将用户信息（用户名、权限、扩展数据）嵌入 Token payload

JJWT 提供了类型安全的 JWT 构建器和解析器 API，支持 HMAC-SHA 签名算法。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明版本 0.12.6 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### 配置项

```properties
# application.properties
codingapi.security.jwt.enable=true
codingapi.security.jwt.secret-key=your-secret-key-at-least-256-bits
codingapi.security.jwt.valid-time=3600000
codingapi.security.jwt.rest-time=1800000
```

### 核心类

- `JwtTokenGateway` — JWT Token 的创建与解析核心逻辑
- `JWTTokenGatewayImpl` — 实现 `TokenGateway` 接口的适配器
- `JWTSecurityConfiguration` — Spring Boot 自动配置类

## 使用实例

### 1. 创建 JWT Token

```java
// JwtTokenGateway.java
public class JwtTokenGateway {
    private final SecretKey key;
    private final int validTime;
    private final int restTime;

    public JwtTokenGateway(SecurityJWTProperties properties) {
        // 使用 HMAC-SHA 密钥
        this.key = Keys.hmacShaKeyFor(
            properties.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
        this.validTime = properties.getValidTime();
        this.restTime = properties.getRestTime();
    }

    public Token create(String username, String iv, List<String> authorities, String extra) {
        Token token = new Token(username, iv, extra, authorities, validTime, restTime);
        // 将 Token 对象序列化为 JSON 作为 JWT subject
        String jwt = Jwts.builder()
            .subject(token.toJson())
            .signWith(key)
            .compact();
        token.setToken(jwt);
        return token;
    }
}
```

### 2. 解析 JWT Token

```java
// JwtTokenGateway.java
public Token parser(String sign) {
    try {
        // 验证签名并解析 Claims
        Jws<Claims> jws = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(sign);
        if (jws != null) {
            String subject = jws.getPayload().getSubject();
            return JSONObject.parseObject(subject, Token.class);
        }
        throw new LocaleMessageException("token.error", "token失效,请重新登录.");
    } catch (Exception exp) {
        throw new LocaleMessageException("token.error", exp.getMessage());
    }
}
```

### 3. 自动配置注册

```java
// JWTSecurityConfiguration.java
@Configuration
@ConditionalOnProperty(prefix = "codingapi.security.jwt", name = "enable", havingValue = "true")
public class JWTSecurityConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "codingapi.security.jwt")
    public SecurityJWTProperties securityJWTProperties() {
        return new SecurityJWTProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenGateway jwtTokenGateway(SecurityJWTProperties properties) {
        return new JwtTokenGateway(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenGateway jwtTokenGatewayImpl(JwtTokenGateway jwtTokenGateway) {
        return new JWTTokenGatewayImpl(jwtTokenGateway);
    }
}
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter-security | JWT Token 生成/解析、自动配置、TokenGateway 实现 |
