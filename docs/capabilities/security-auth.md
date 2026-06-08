---
name: security-auth
description: 基于 Spring Security 的 JWT/Redis 双模式认证框架，支持 Token 自动续期与登录扩展
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

Spring Security 默认的表单登录和 Session 机制不适合前后端分离的 RESTful API 场景。企业应用通常需要：
- 无状态的 JWT Token 认证或有状态的 Redis Token 认证
- JSON 格式的统一登录/登出响应（而非重定向）
- Token 自动续期机制
- 可扩展的登录前置/后置处理逻辑
- 统一的未认证、无权限错误响应

`springboot-starter-security` 在 Spring Security 基础上封装了完整的认证框架，提供开箱即用的 JWT / Redis 双模式认证，所有安全事件均以 JSON 格式返回统一的 `Response` 结构。

## 如何使用

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-security</artifactId>
</dependency>
```

### 2. 配置安全属性

在 `application.properties` 中配置：

```properties
# 安全模块配置前缀
codingapi.security.login-processing-url=/login      # 登录接口路径
codingapi.security.logout-url=/logout                # 登出接口路径
codingapi.security.ignore-urls=/open/**,/#/**        # 免认证 URL 列表
codingapi.security.authenticated-urls=/api/**        # 需认证的 URL 列表
codingapi.security.disable-cors=false                # 是否禁用 CORS（false=启用默认 CORS）

# JWT 模式
codingapi.security.jwt.enable=true                   # 启用 JWT 认证

# Redis 模式
codingapi.security.redis.enable=true                 # 启用 Redis 有状态认证
```

配置通过 `CodingApiSecurityProperties` 绑定。

### 3. Token 网关 (TokenGateway)

框架定义了 `TokenGateway` 接口作为 Token 操作的抽象层：

```java
public interface TokenGateway {
    // 创建 Token
    Token create(String username, String iv, List<String> authorities, String extra);
    
    // 解析 Token
    Token parser(String sign);
}
```

框架提供了两种实现：
- **`JWTTokenGatewayImpl`**：基于 JJWT 的无状态 JWT Token
- **`RedisTokenGatewayImpl`**：基于 Redis 的有状态 Token

根据配置自动选择实现。

### 4. Token 对象

`Token` 类封装了认证信息：

```java
public class Token implements JsonSerializable {
    private String username;       // 用户名
    private String extra;          // 额外信息（JSON 字符串）
    private String iv;             // 加密向量（AES 编码）
    private String token;          // Token 值
    private List<String> authorities; // 权限列表
    private long expireTime;       // 过期时间戳
    private long remindTime;       // 提醒续期时间戳
    
    // 验证 Token 是否过期
    void verify() throws TokenExpiredException;
    
    // 是否需要续期（未过期且已过提醒时间）
    boolean canRestToken();
    
    // 解析额外信息为指定类型
    <T> T parseExtra(Class<T> clazz);
    
    // 转换为 Spring Security Authentication
    UsernamePasswordAuthenticationToken getAuthenticationToken();
}
```

### 5. 获取当前用户信息

通过 `TokenContext` 获取当前登录用户的 Token：

```java
// 获取当前 Token
Token token = TokenContext.current();
String username = token.getUsername();
List<String> authorities = token.getAuthorities();

// 获取额外信息
String extra = TokenContext.getExtra();
UserInfo userInfo = token.parseExtra(UserInfo.class);
```

### 6. 自定义登录处理

实现 `SecurityLoginHandler` 接口以扩展登录逻辑：

```java
@Bean
public SecurityLoginHandler securityLoginHandler() {
    return new SecurityLoginHandler() {
        @Override
        public void preHandle(HttpServletRequest request, 
                              HttpServletResponse response, 
                              LoginRequest loginRequest) throws Exception {
            // 登录前置处理：验证码校验、IP 黑名单检查等
            if (!captchaService.verify(loginRequest.getCaptcha())) {
                throw new Exception("验证码错误");
            }
        }

        @Override
        public LoginResponse postHandle(HttpServletRequest request,
                                        HttpServletResponse response,
                                        LoginRequest loginRequest,
                                        UserDetails user,
                                        Token token) {
            // 登录后置处理：记录登录日志、附加额外信息等
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token.getToken());
            loginResponse.setUsername(token.getUsername());
            loginResponse.setAuthorities(token.getAuthorities());
            loginResponse.setData(Map.of("lastLoginTime", System.currentTimeMillis()));
            return loginResponse;
        }
    };
}
```

`LoginResponse` 结构：
```java
public class LoginResponse {
    private String username;
    private String token;
    private List<String> authorities;
    private Object data;   // 自定义附加数据
}
```

### 7. 自定义 Token 过滤

实现 `AuthenticationTokenFilter` 在每次请求认证通过后执行额外逻辑：

```java
@Bean
public AuthenticationTokenFilter authenticationTokenFilter() {
    return (request, response) -> {
        // 例如：更新最后登录时间、审计日志等
    };
}
```

### 8. 自定义 HttpSecurity 配置

实现 `HttpSecurityCustomer` 接口以进一步定制 Spring Security 配置：

```java
@Bean
public HttpSecurityCustomer httpSecurityCustomer(CodingApiSecurityProperties properties) {
    return new DefaultHttpSecurityCustomer(properties);
    // 或自定义实现
}
```

## 使用实例

### 认证过滤器链工作原理

```
HTTP Request
    ↓
MyUnAuthenticationEntryPoint     ← 未认证时返回 {"errCode":"not.login","errMessage":"please to login."}
    ↓
MyAccessDeniedHandler            ← 无权限时返回 {"errCode":"not.access","errMessage":"please check user authentication."}
    ↓
MyLoginFilter (POST /login)      ← 登录认证
    ├── attemptAuthentication()  → 读取 JSON body → SecurityLoginHandler.preHandle() → authenticate()
    ├── successfulAuthentication() → TokenGateway.create() → SecurityLoginHandler.postHandle() → 返回 LoginResponse
    └── unsuccessfulAuthentication() → 返回 {"errCode":"login.error","errMessage":"..."}
    ↓
MyAuthenticationFilter           ← Token 认证
    ├── 匹配 authenticatedUrls
    ├── 从 Header "Authorization" 读取 Token
    ├── TokenGateway.parser(sign) → Token.verify()
    ├── Token.canRestToken() → 自动续期，新 Token 写入响应头
    └── SecurityContextHolder.setAuthentication()
    ↓
MyLogoutSuccessHandler (POST /logout) ← 登出成功返回 {"success":true}
```

### 统一错误响应格式

所有安全相关的错误都以统一的 `Response` JSON 格式返回：

| 场景 | errCode | errMessage |
|------|---------|------------|
| 未登录 | `not.login` | `please to login.` |
| 无权限 | `not.access` | `please check user authentication.` |
| 登录失败 | `login.error` | 具体异常信息 |
| Token 为空 | `token.error` | `token must not null.` |
| Token 过期 | `token.expire` | `token expire.` |

### Token 自动续期

当 Token 未过期但已超过 `remindTime` 时，`MyAuthenticationFilter` 会自动创建新 Token 并通过响应头 `Authorization` 返回：

```java
// MyAuthenticationFilter.doFilterInternal() 中的关键逻辑
if (token.canRestToken()) {
    Token newSign = tokenGateway.create(
        token.getUsername(), 
        token.decodeIv(), 
        token.getAuthorities(), 
        token.getExtra()
    );
    response.setHeader("Authorization", newSign.getToken());
}
```

客户端应在每次响应后检查 `Authorization` 响应头，如有新值则替换本地存储的 Token。

### 密码编码器

框架默认使用 Spring Security 的 `DelegatingPasswordEncoder`，支持多种编码格式的自动识别：

```java
@Bean
@ConditionalOnMissingBean
public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}
```

如需自定义密码编码器，只需声明自己的 `PasswordEncoder` Bean 即可覆盖默认实现。

### 默认用户（开发环境）

当没有自定义 `UserDetailsService` 时，框架自动创建内存用户：
- `admin / admin` — ROLE_ADMIN
- `user / admin` — ROLE_USER

生产环境应提供自己的 `UserDetailsService` Bean 来覆盖此默认行为。
