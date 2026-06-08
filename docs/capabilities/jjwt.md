---
name: jjwt
description: JJWT 库 — JWT Token 的创建、签名、解析与验证
status: 已实现
scope: 后端
source: 框架:JJWT
import: io.jsonwebtoken:jjwt-api
framework_version: 0.12.6
---

## 解决什么问题

JJWT（Java JWT）是 Java 生态最主流的 JWT 库，本框架在 `springboot-starter-security` 中使用 JJWT 实现 Token 认证：

- **Token 创建**：`Jwts.builder()` 构建带签名的 JWT
- **Token 解析**：`Jwts.parser()` 验证签名并提取 Claims
- **HMAC 签名**：使用 `Keys.hmacShaKeyFor()` 生成对称密钥
- **Subject 载荷**：将用户信息序列化到 Token 的 subject 字段

## 如何使用

### 创建 Token

```java
SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
String jwt = Jwts.builder()
    .subject(tokenPayload)
    .signWith(key)
    .compact();
```

### 解析 Token

```java
Jws<Claims> jws = Jwts.parser()
    .verifyWith(key)
    .build()
    .parseSignedClaims(jwtString);
String subject = jws.getPayload().getSubject();
```

### 本框架封装

```java
// 通过 JwtTokenGateway 封装
JwtTokenGateway gateway = new JwtTokenGateway(securityJWTProperties);
Token token = gateway.create(username, authorities);
Token parsed = gateway.parser(jwtString);
```

## 使用实例

```java
// 手动使用 JJWT
SecretKey key = Keys.hmacShaKeyFor("my-secret-key-at-least-32-chars".getBytes());

// 创建
String jwt = Jwts.builder()
    .subject("{\"userId\":1,\"username\":\"admin\"}")
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + 7200000))
    .signWith(key)
    .compact();

// 验证
Claims claims = Jwts.parser().verifyWith(key).build()
    .parseSignedClaims(jwt).getPayload();
String payload = claims.getSubject();
```
