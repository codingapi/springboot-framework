---
name: spring-security
description: Spring Security 安全认证与授权能力，框架在此基础上封装了 JWT 无状态认证和 Redis 有状态认证两种模式
status: 已实现
scope: 后端
source: 框架:spring-security
framework_version: Managed by Spring Boot BOM (3.3.5)
---

## 解决什么问题

Spring Security 为框架提供了完整的安全基础设施，解决了以下问题：

- **认证（Authentication）**：框架的 `springboot-starter-security` 模块基于 Spring Security 过滤器链，实现了两种 Token 认证模式：
  - **JWT 无状态模式**：通过 `JwtTokenGateway` + JJWT 库签发/验证 JWT Token，适用于分布式部署
  - **Redis 有状态模式**：通过 `RedisTokenGateway` + `RedisTemplate` 将 Token 存储在 Redis 中，支持服务端主动失效、单点登录等场景
- **授权（Authorization）**：基于 Spring Security 的权限模型，`Token.authorities` 携带用户角色/权限列表，通过 `UsernamePasswordAuthenticationToken` 注入 SecurityContext
- **CSRF 防护**：REST API 场景下默认禁用 CSRF（无状态 Token 认证不需要），表单场景可按需启用
- **统一安全过滤链**：`MyAuthenticationFilter` 拦截请求解析 Token，`MyLoginFilter` 处理登录请求生成 Token，`SecurityLoginHandler` 统一登录成功/失败响应

## 如何使用

### 依赖引入

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-security</artifactId>
</dependency>
```

### 配置认证模式

在 `application.properties` 中选择认证方式：

```properties
# JWT 无状态认证
codingapi.security.jwt.enable=true
codingapi.security.jwt.secret-key=your-secret-key-at-least-256-bits
codingapi.security.jwt.valid-time=86400000     # Token 有效期（毫秒）
codingapi.security.jwt.rest-time=3600000       # Token 刷新提醒时间（毫秒）

# Redis 有状态认证（二选一）
codingapi.security.redis.enable=true
codingapi.security.redis.valid-time=86400000
codingapi.security.redis.rest-time=3600000

# 免认证 URL 列表
codingapi.security.ignore-urls=/open/**,/#/**,/api/login
```

### TokenGateway 接口

两种模式均实现统一的 `TokenGateway` 接口，业务代码无需关心底层存储：

```java
public interface TokenGateway {
    Token create(String username, String iv, List<String> authorities, String extra);
    Token parser(String sign);
}
```

## 使用实例

### 自定义登录逻辑

```java
@Service
@AllArgsConstructor
public class LoginService {

    private final TokenGateway tokenGateway;

    public Token login(String username, String password) {
        // 1. 验证用户名密码（自行实现）
        UserDetails user = authenticate(username, password);

        // 2. 生成 Token（自动根据配置选择 JWT 或 Redis 模式）
        return tokenGateway.create(
            user.getUsername(),
            null,                              // iv（AES 加密向量，可选）
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()),
            "{\"userId\":1}"                   // extra 扩展信息
        );
    }
}
```

### 获取当前用户信息

```java
@RestController
public class ProfileController {

    @GetMapping("/api/profile")
    public SingleResponse<Map<String, Object>> profile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Token token = (Token) auth.getPrincipal();

        Map<String, Object> info = new HashMap<>();
        info.put("username", token.getUsername());
        info.put("authorities", token.getAuthorities());
        info.put("extra", token.parseExtra(Map.class));
        return SingleResponse.of(info);
    }
}
```
