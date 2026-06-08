---
name: spring-data-redis
description: Redis 客户端抽象，支持缓存和有状态认证 Token 存储
status: 已实现
scope: 后端
source: 框架:Spring Data Redis
---

## 解决什么问题

Spring Data Redis 为项目提供 Redis 访问能力，解决了以下问题：

- **有状态认证**：在 Redis 模式下存储用户 Token 和会话信息，支持分布式部署
- **缓存**：为热点数据提供高速缓存层，减少数据库压力
- **分布式协调**：可作为分布式锁、消息队列等基础设施的底层支撑

## 如何使用

项目使用 `spring-boot-starter-data-redis`（随 Spring Boot 3.3.5），主要在安全模块中使用：

1. **启用 Redis 认证**：配置 `codingapi.security.redis.enable=true`
2. **Token 存储**：登录成功后将 Token 存入 Redis，后续请求从 Redis 验证 Token 有效性
3. **与 JWT 互斥**：JWT 模式（无状态）和 Redis 模式（有状态）二选一
4. **连接配置**：通过标准 Spring Boot Redis 配置项指定连接信息

## 使用实例

### 启用 Redis 有状态认证

```properties
# application.properties
codingapi.security.redis.enable=true
codingapi.security.jwt.enable=false

# Redis 连接配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your-password
```

### 免认证 URL 配置

```properties
# 这些路径不需要 Token 验证
codingapi.security.ignore-urls=/open/**,/#/**,/api/query/**
```

### 架构说明

```
用户登录 → MyLoginFilter 验证凭据
         → 生成 Token 存入 Redis
         → 返回 Token 给客户端

后续请求 → MyAuthenticationFilter 拦截
         → 从 Redis 读取 Token 验证
         → 验证通过则放行，否则返回 401
```
