---
name: spring-data-redis
description: Spring Data Redis 数据存储能力，框架中主要用于 Token/Session 的有状态存储，支持服务端主动失效和多端登录管理
status: 已实现
scope: 后端
source: 框架:spring-data-redis
framework_version: Managed by Spring Boot BOM (3.3.5)
---

## 解决什么问题

Spring Data Redis 在框架中承担了有状态认证场景下的核心存储职责，解决了以下问题：

- **Token 服务端存储**：`RedisTokenGateway` 使用 `RedisTemplate<String, String>` 将 Token 以 JSON 格式存入 Redis，Key 格式为 `{encodedUsername}:{uuid}`，支持按用户名批量查询和删除
- **Token 生命周期管理**：通过 Redis TTL 机制自动过期，同时支持服务端主动调用 `removeToken()` / `resetToken()` 实现即时失效和续期
- **多端登录管理**：`getTokensByUsername()` 获取用户所有在线 Token，`removeUsername()` 一键踢出所有设备，还支持按条件（`Predicate<Token>`）选择性踢出
- **缓存抽象**：可作为通用缓存层，用于脚本编译结果缓存、会话数据暂存等场景

## 如何使用

### 依赖引入

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-security</artifactId>
</dependency>
```

`spring-boot-starter-data-redis` 作为 `provided` 依赖包含在安全模块中，启用 Redis 认证模式时需确保运行时存在 Redis 连接配置。

### 配置 Redis 连接

```properties
# Redis 连接配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your-password

# 启用 Redis 有状态认证
codingapi.security.redis.enable=true
codingapi.security.redis.valid-time=86400000     # Token 有效期（毫秒）
codingapi.security.redis.rest-time=3600000       # 刷新提醒时间（毫秒）
```

### RedisTokenGateway 核心 API

框架封装了 `RedisTokenGateway`，提供面向业务的 Token 操作方法：

| 方法 | 说明 |
|------|------|
| `create(username, iv, authorities, extra)` | 创建 Token 并存入 Redis |
| `getToken(token)` | 根据 Token 字符串获取用户信息 |
| `removeToken(token)` | 删除指定 Token |
| `resetToken(token)` | 重置 Token 有效期 |
| `removeUsername(username)` | 删除用户所有 Token（踢出所有设备） |
| `getTokensByUsername(username)` | 获取用户所有在线 Token Key |
| `removeUsername(username, predicate)` | 按条件选择性删除 Token |

## 使用实例

### 管理员强制下线用户

```java
@Service
@AllArgsConstructor
public class UserAdminService {

    private final RedisTokenGateway redisTokenGateway;

    /**
     * 强制下线指定用户的所有设备
     */
    public void forceLogout(String username) {
        redisTokenGateway.removeUsername(username);
    }

    /**
     * 选择性下线：仅移除特定设备的 Token
     */
    public void logoutDevice(String username, String targetToken) {
        redisTokenGateway.removeUsername(username, token ->
            targetToken.equals(token.getToken())
        );
    }

    /**
     * 查看用户在线设备数
     */
    public int getOnlineDeviceCount(String username) {
        return redisTokenGateway.getTokensByUsername(username).size();
    }
}
```

### Token 续期

```java
// 在认证过滤器中检测 Token 是否需要续期
Token token = tokenGateway.parser(tokenString);
if (token != null && token.canRestToken()) {
    // canRestToken() = !isExpire() && remindTime <= currentTimeMillis
    redisTokenGateway.resetToken(token);
}
```
