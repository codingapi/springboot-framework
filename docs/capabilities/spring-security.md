---
name: spring-security
description: 认证授权框架，提供 Filter Chain、CSRF 防护
status: 已实现
scope: 后端
source: 框架:Spring Security
---

## 解决什么问题

Spring Security 为项目提供安全基础设施，解决了以下问题：

- **认证**：验证用户身份，支持 JWT 无状态认证和 Redis 有状态认证两种模式
- **授权**：基于 URL 模式和角色的访问控制
- **Filter Chain**：通过过滤器链拦截请求，统一处理安全逻辑
- **密码加密**：提供 PasswordEncoder 接口进行安全的密码哈希

## 如何使用

项目使用 `spring-boot-starter-security`（随 Spring Boot 3.3.5），被 `springboot-starter-security` 模块封装：

1. **JWT 模式**：配置 `codingapi.security.jwt.enable=true`，使用 `JwtTokenGateway` 签发和验证 Token
2. **Redis 模式**：配置 `codingapi.security.redis.enable=true`，Token 存储在 Redis 中
3. **免认证 URL**：通过 `codingapi.security.ignore-urls` 配置白名单路径
4. **自定义 Filter**：框架提供 `MyAuthenticationFilter`、`MyLoginFilter`、`MyLogoutSuccessHandler` 等组件
5. **异常处理**：`MyAccessDeniedHandler` 和 `MyUnAuthenticationEntryPoint` 统一返回 `Response` 格式的错误响应

## 使用实例

### 安全配置项

```properties
# application.properties
codingapi.security.jwt.enable=true
codingapi.security.redis.enable=false
codingapi.security.ignore-urls=/open/**,/#/**,/api/query/**
```

### Controller 中使用认证信息

```java
@RestController
@RequestMapping("/api/cmd/user")
@AllArgsConstructor
public class UserDomainCmdController {

    private final UserRouter userRouter;

    @PostMapping("/save")
    public Response save(@RequestBody UserCmd.UpdateRequest request) {
        // 请求已通过 Security Filter Chain 认证
        userRouter.createOrUpdate(request);
        return Response.buildSuccess();
    }
}
```

### Domain 层定义密码加密网关

```java
// domain 层定义接口，不依赖 Spring Security
public interface PasswordEncoder {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
```
