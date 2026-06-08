---
name: jwt-auth-gateway
description: JWT 认证网关，集成 Spring Security 与 JJWT，支持 Token 创建、解析、Redis 有状态/无状态双模式
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter-security
symbols:
  - JwtTokenGateway
  - JWTTokenGatewayImpl
  - JWTSecurityConfiguration
  - SecurityJWTProperties
  - Token
  - TokenContext
  - TokenGateway
  - RedisTokenGateway
  - RedisTokenGatewayImpl
  - AuthenticationTokenFilter
  - HttpSecurityConfigurer
  - WebSecurityConfigurer
  - LoginRequest
  - LoginResponse
content_hash: 2dfa771d38597c44f2723f8f37c86531e625dc39fc7d352eae74844d43c949f3
---

## 解决什么问题

Web 应用需要标准化的用户认证机制。本能力在 Spring Security 基础上封装了 JWT 认证体系：

- **无状态认证（JWT 模式）**：Token 自包含用户信息，适合微服务架构
- **有状态认证（Redis 模式）**：Token 存储在 Redis 中，支持主动注销和续期
- **统一网关抽象**：`TokenGateway` 接口屏蔽 JWT/Redis 两种模式的差异
- **Spring Security 集成**：自动配置 Filter 链，处理登录、登出、Token 校验

## 如何使用

### 配置

```properties
# 启用 JWT 无状态认证
codingapi.security.jwt.enable=true
codingapi.security.jwt.secret-key=your-secret-key-at-least-32-chars
codingapi.security.jwt.valid-time=7200

# 或启用 Redis 有状态认证
codingapi.security.redis.enable=true
codingapi.security.redis.valid-time=7200
```

### 创建 Token

```java
@Autowired
private TokenGateway tokenGateway;

// 登录成功后创建 Token
Token token = tokenGateway.create(username, authorities);
// 或通过 JwtTokenGateway 创建
Token token = jwtTokenGateway.create(username, iv, authorities, extra);
```

### 解析 Token

```java
Token token = jwtTokenGateway.parser(jwtString);
String username = token.getUsername();
List<String> authorities = token.getAuthorities();
```

### 配置免认证 URL

```properties
codingapi.security.ignore-urls=/open/**,/#/**,/api/version
```

## 使用实例

```java
// 自定义登录处理
@Service
public class LoginService {
    @Autowired
    private TokenGateway tokenGateway;

    public LoginResponse login(String username, String password) {
        // 验证用户名密码...
        UserDetails user = authenticate(username, password);
        Token token = tokenGateway.create(
            user.getUsername(), 
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList()
        );
        return new LoginResponse(token.getToken(), token.getValidTime());
    }
}
```
