---
name: jsonwebtoken/jjwt
module: jsonwebtoken
description: JJWT 库，提供 JWT 令牌生成、验证和解析
status: 已实现
scope: 后端
source: 框架:jsonwebtoken
import: "io.jsonwebtoken:jjwt-api"
framework_version: 0.12.6
---

## 解决什么问题

在微服务与前后端分离架构中，服务端需要一种无状态的认证机制来识别用户身份。传统 Session 方案依赖服务端存储，难以水平扩展且不适合移动端场景。

JJWT（Java JSON Web Token）提供了标准的 JWT 令牌生成、签名与解析能力，解决以下核心问题：

- **无状态认证**：将用户信息（用户名、权限、额外数据）编码进自包含的 JWT 令牌中，服务端无需维护会话存储即可验证请求身份。
- **防篡改签名**：通过 HMAC-SHA 算法对令牌进行签名，确保令牌内容在传输过程中不被伪造或修改。
- **令牌生命周期管理**：支持配置有效期与续期提醒时间，配合框架的 `Token.canRestToken()` 机制实现自动续期，避免用户在活跃操作中频繁重新登录。
- **统一令牌抽象**：框架通过 `TokenGateway` 接口封装了 JWT 与 Redis 两种令牌策略，业务层只需面向接口编程，切换认证模式无需改动业务代码。

## 如何使用

### 依赖引入

JJWT 作为 `springboot-starter-security` 模块的传递依赖自动引入，无需单独声明：

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-security</artifactId>
</dependency>
```

### 启用 JWT 认证

在 `application.properties` 中开启 JWT 并配置参数：

```properties
# 启用 JWT 认证（默认 true）
codingapi.security.jwt.enable=true

# HMAC 签名密钥（需大于 32 位字符串）
codingapi.security.jwt.secret-key=your-secret-key-at-least-32-characters

# 令牌有效期（毫秒），默认 15 分钟
codingapi.security.jwt.valid-time=900000

# 令牌续期提醒阈值（毫秒），默认 10 分钟后触发续期
codingapi.security.jwt.rest-time=600000
```

### 核心 API

框架通过 `TokenGateway` 接口对外暴露令牌操作，JWT 模式下由 `JWTTokenGatewayImpl` → `JwtTokenGateway` 实现：

| 方法 | 说明 |
|------|------|
| `create(username, authorities)` | 创建基础令牌，仅包含用户名和权限列表 |
| `create(username, authorities, extra)` | 创建带额外数据的令牌，extra 以 JSON 字符串存入 subject |
| `create(username, iv, authorities)` | 创建带加密向量（iv）的令牌，用于加解密场景 |
| `create(username, iv, authorities, extra)` | 完整创建方法，同时携带 iv 和额外数据 |
| `parser(sign)` | 解析并验证 JWT 签名，返回 `Token` 对象；签名无效或格式错误时抛出 `LocaleMessageException` |

`Token` 对象提供以下辅助方法：

| 方法 | 说明 |
|------|------|
| `verify()` | 校验令牌是否过期，过期则抛出 `TokenExpiredException` |
| `isExpire()` | 判断令牌是否已过期 |
| `canRestToken()` | 判断是否需要续期（未过期但已超过 restTime 阈值） |
| `parseExtra(Class<T>)` | 将 extra 字段反序列化为指定类型 |
| `decodeIv()` | AES 解密 iv 字段 |
| `getAuthenticationToken()` | 转换为 Spring Security 的 `UsernamePasswordAuthenticationToken` |

### 自动装配

当 `codingapi.security.jwt.enable=true` 时，`JWTSecurityConfiguration` 自动注册以下 Bean：

- `SecurityJWTProperties` — JWT 配置属性
- `JwtTokenGateway` — JWT 令牌生成与解析的核心组件
- `TokenGateway`（`JWTTokenGatewayImpl`）— 统一的令牌网关接口实现

若项目中已存在自定义 `TokenGateway` Bean，自动装配不会覆盖（`@ConditionalOnMissingBean`）。

## 使用实例

### 注入 TokenGateway 创建与解析令牌

```java
@Service
public class AuthService {

    private final TokenGateway tokenGateway;

    public AuthService(TokenGateway tokenGateway) {
        this.tokenGateway = tokenGateway;
    }

    /**
     * 用户登录后签发 JWT 令牌
     */
    public Token login(String username, List<String> authorities) {
        // 基础令牌
        return tokenGateway.create(username, authorities);
    }

    /**
     * 签发携带额外业务数据的令牌
     */
    public Token loginWithExtra(String username, List<String> authorities, String tenantId) {
        String extra = "{\"tenantId\":\"" + tenantId + "\"}";
        return tokenGateway.create(username, authorities, extra);
    }

    /**
     * 从请求头解析并验证令牌
     */
    public Token validateToken(String jwtSign) {
        // 验签 + 解析，失败抛出 LocaleMessageException
        Token token = tokenGateway.parser(jwtSign);
        // 检查是否过期
        token.verify();
        return token;
    }
}
```

### 在过滤器中使用令牌

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenGateway tokenGateway;

    public JwtAuthFilter(TokenGateway tokenGateway) {
        this.tokenGateway = tokenGateway;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String sign = authHeader.substring(7);
            try {
                Token token = tokenGateway.parser(sign);
                token.verify();

                // 检查是否需要续期
                if (token.canRestToken()) {
                    // 重新签发令牌并通过响应头返回
                    Token newToken = tokenGateway.create(
                        token.getUsername(), token.getAuthorities(), token.getExtra());
                    response.setHeader("X-New-Token", newToken.getToken());
                }

                // 设置 Spring Security 认证上下文
                SecurityContextHolder.getContext()
                    .setAuthentication(token.getAuthenticationToken());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
```

### 解析令牌中的额外数据

```java
// 定义业务数据结构
public class UserContext {
    private String tenantId;
    private String department;
    // getter/setter
}

// 从令牌中提取
Token token = tokenGateway.parser(jwtSign);
UserContext ctx = token.parseExtra(UserContext.class);
String tenantId = ctx.getTenantId();
```
