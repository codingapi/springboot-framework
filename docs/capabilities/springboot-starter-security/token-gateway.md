---
name: springboot-starter-security/token-gateway
module: springboot-starter-security
description: 无状态 JWT 与有状态 Redis 双模 Token 网关，统一 Token 生命周期、过期/续期与权限注入
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-security"
symbols:
  - Token
  - TokenContext
  - TokenGateway
  - RedisTokenGateway
content_hash: 36e7a0811ec5e56eb2b6b288446d5330ed912b99789cde58a560ccc3819d2afc
---

## 解决什么问题

权限系统面临两种典型部署：

- **无状态（JWT）**：适合分布式/移动端，无需服务端存储，但难以即时吊销
- **有状态（Redis）**：服务端可即时吊销/踢人，但需要共享存储

本能力提供 `TokenGateway` 抽象，内置两种实现（`JwtTokenGateway` / `RedisTokenGateway`），业务代码无需关心底层是 JWT 还是 Redis，统一通过 `TokenContext.current()` 拿到当前用户。

`Token` 对象本身承载：
- 用户名（`username`）
- 权限列表（`authorities`）
- IV/extra 加密字段
- 过期时间（`expireTime`）/ 续期时间（`remindTime`）
- 是否可以续期（`canRestToken()`：未过期 + 已到续期点）

## 如何使用

**1. 配置（选其一）**

```yaml
# 启用 Redis 有状态 Token
spring:
  data:
    redis:
      host: localhost
      port: 6379

codingapi:
  security:
    token:
      type: redis   # 或 jwt
      redis:
        valid-time: 3600000      # 1 小时
        rest-time: 1800000       # 30 分钟前可续
      jwt:
        secret: your-256-bit-secret
        expire-time: 7200
```

**2. 创建 Token**

```java
TokenGateway tokenGateway;  // 框架自动注入
List<String> authorities = List.of("ROLE_USER", "ORDER:READ");

Token token = tokenGateway.create("alice", "iv-encrypted", authorities, "extra-json");
String sign = token.getToken();  // 客户端后续请求携带
```

**3. 解析当前 Token**

```java
@RestController
public class UserController {
    @GetMapping("/me")
    public User me() {
        Token current = TokenContext.current();
        return userService.findByUsername(current.getUsername());
    }
}
```

**4. 检查过期与续期**

```java
Token token = tokenGateway.parser(sign);
token.verify();  // 过期时抛 TokenExpiredException

if (token.canRestToken()) {
    tokenGateway.resetToken(token);  // Redis 实现：续期；JWT 实现：需重新签发
}
```

**5. 按用户名踢人（仅 Redis）**

```java
redisTokenGateway.removeUsername("alice");  // 删除该用户所有 token
// 或自定义过滤条件
redisTokenGateway.removeUsername("alice", t -> t.getAuthorities().contains("ROLE_GUEST"));
```

**6. 与 Spring Security 集成**

`Token.getAuthenticationToken()` 返回 `UsernamePasswordAuthenticationToken`，由 `MyAuthenticationFilter` 写入 `SecurityContextHolder`，因此 `TokenContext.current()` 实际就是从 `SecurityContext` 中取出 `Token` 对象。

## 使用实例

```java
// 1. 登录过滤器创建 Token
public class MyLoginFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    protected void successfulAuthentication(...) {
        UserDetails user = (UserDetails) authResult.getPrincipal();
        Token token = tokenGateway.create(
            user.getUsername(),
            loginRequest.getPassword(),  // 用密码当 IV
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
            TokenContext.getExtra()
        );
        // 返回 token 给客户端
    }
}

// 2. 业务接口读取当前用户
@GetMapping("/api/orders")
public List<Order> myOrders() {
    Token token = TokenContext.current();
    String username = token.getUsername();
    List<String> auths = token.getAuthorities();  // 包含 ROLE_*
    return orderService.findByUser(username, auths);
}

// 3. 解析 extra 字段（前端传入的加密数据）
@PostMapping("/api/decrypt")
public DecryptedData decrypt() {
    Token token = TokenContext.current();
    MyExtra extra = token.parseExtra(MyExtra.class);  // fastjson 反序列化
    return new DecryptedData(token.decodeIv(), extra);
}
```
