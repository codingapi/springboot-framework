---
name: token-gateway
description: JWT/Redis 双模式 Token 认证网关，统一创建、解析和管理认证令牌
status: 已实现
scope: 后端
source: 项目自有
last_commit: 327e5420
code_files:
  - springboot-starter-security/src/main/java/com/codingapi/springboot/security/gateway/TokenGateway.java
  - springboot-starter-security/src/main/java/com/codingapi/springboot/security/gateway/Token.java
  - springboot-starter-security/src/main/java/com/codingapi/springboot/security/jwt/JwtTokenGateway.java
  - springboot-starter-security/src/main/java/com/codingapi/springboot/security/redis/RedisTokenGateway.java
  - springboot-starter-security/src/main/java/com/codingapi/springboot/security/gateway/TokenContext.java
---

## 解决什么问题

在多端登录、无状态/有状态认证场景中，业务层需要一个统一的 Token 抽象来屏蔽底层存储差异。`TokenGateway` 提供了统一的令牌创建、解析和生命周期管理接口，支持：

- **JWT 无状态模式**：Token 信息自包含在签名载荷中，适合微服务和分布式场景
- **Redis 有状态模式**：Token 存储在 Redis 中，支持多端登录管理、强制下线、按需踢出

## 如何使用

### 核心接口

```java
public interface TokenGateway {
    // 创建令牌（用户名 + IV加密密钥 + 权限列表 + 扩展信息）
    Token create(String username, String iv, List<String> authorities, String extra);

    // 解析令牌，返回 Token 对象
    Token parser(String sign);
}
```

### Token 对象

`Token` 包含以下字段：
- `username` — 用户名
- `token` — 令牌字符串（JWT 签名或 Redis Key）
- `authorities` — 权限列表
- `extra` — 扩展信息（JSON 字符串，可通过 `parseExtra(Class<T>)` 反序列化）
- `iv` — AES 加密密钥（经 AESTools 编码）
- `expireTime` — 过期时间戳
- `remindTime` — 续期提醒时间戳

关键方法：
- `token.verify()` — 校验是否过期，过期抛出 `TokenExpiredException`
- `token.canRestToken()` — 判断是否需要续期（未过期且已过提醒时间）
- `token.getAuthenticationToken()` — 转换为 Spring Security 的 `UsernamePasswordAuthenticationToken`

### JWT 模式（JwtTokenGateway）

通过 HMAC-SHA 签名生成自包含 JWT，配置项：
```properties
codingapi.security.jwt.secret-key=your-secret-key
codingapi.security.jwt.valid-time=3600000    # 有效期（毫秒）
codingapi.security.jwt.rest-time=1800000     # 续期提醒时间（毫秒）
```

### Redis 模式（RedisTokenGateway）

Token 以 `AES(用户名):UUID` 为 Key 存储在 Redis 中，支持：
- `getToken(token)` — 根据令牌获取用户信息
- `removeToken(token)` — 删除单个令牌
- `removeUsername(username)` — 删除用户所有令牌（强制下线）
- `removeUsername(username, Predicate)` — 按条件选择性踢出
- `getTokensByUsername(username)` — 获取用户所有令牌（多端管理）
- `resetToken(token)` — 续期令牌

```properties
codingapi.security.redis.valid-time=3600000
codingapi.security.redis.rest-time=1800000
```

## 使用实例

```java
// JWT 模式 — 创建并解析令牌
JwtTokenGateway jwtGateway = new JwtTokenGateway(jwtProperties);
Token token = jwtGateway.create("admin", Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
System.out.println(token.getToken()); // eyJhbGciOiJIUz...

Token parsed = jwtGateway.parser(token.getToken());
parsed.verify(); // 校验过期时间
System.out.println(parsed.getUsername()); // "admin"

// Redis 模式 — 多端登录管理
RedisTokenGateway redisGateway = new RedisTokenGateway(redisTemplate, redisProperties);
Token token1 = redisGateway.create("user1", "iv123", Arrays.asList("ROLE_USER"), null);

// 获取用户所有在线会话
List<String> tokens = redisGateway.getTokensByUsername("user1");

// 按条件踢出（如踢出非当前设备的会话）
redisGateway.removeUsername("user1", t -> !t.getToken().equals(currentToken));

// 强制下线
redisGateway.removeUsername("user1");
```
