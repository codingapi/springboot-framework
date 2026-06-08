---
name: springboot-starter-security/auth-gateway
module: springboot-starter-security
description: 安全认证网关，支持 JWT 无状态认证和 Redis 有状态认证两种模式
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-security"
symbols:
  - TokenGateway
  - TokenContext
  - Token
  - MyLoginFilter
  - MyAuthenticationFilter
content_hash: f387cbcf0fa1de5a7c51bf0d2afb16e342b53d42ad2031f6741cb6db4e4e2625
---

## 解决什么问题

在企业级 Web 应用中，用户认证是安全体系的第一道防线。Spring Security 原生提供的表单登录和 Session 机制不适合前后端分离架构，开发者通常需要自行实现以下能力：

- **Token 生命周期管理**：创建、解析、过期校验、自动续期
- **多认证模式切换**：JWT 无状态模式适合微服务和移动端，Redis 有状态模式适合需要服务端主动踢人的管理后台
- **登录流程定制**：在认证前后插入自定义逻辑（验证码校验、登录日志、额外业务数据注入等）
- **统一 JSON 响应**：认证成功/失败均返回标准 `Response` 格式，而非 Spring Security 默认的重定向或 HTML 页面

auth-gateway 将上述能力封装为开箱即用的安全网关层，通过 `TokenGateway` 策略接口抽象 Token 的创建与解析，配合 `MyLoginFilter`（登录拦截）和 `MyAuthenticationFilter`（请求鉴权拦截）两个 Servlet Filter，实现完整的认证闭环。开发者只需通过配置选择 JWT 或 Redis 模式，即可零代码获得生产级认证能力。

## 如何使用

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-security</artifactId>
</dependency>
```

### 2. 选择认证模式

通过 `application.properties` 启用其中一种模式（二选一）：

**JWT 无状态模式：**

```properties
codingapi.security.jwt.enable=true
codingapi.security.jwt.secret-key=your-secret-key-must-be-at-least-32-chars
codingapi.security.jwt.valid-time=900000    # Token 有效期 15 分钟（毫秒）
codingapi.security.jwt.rest-time=600000     # 10 分钟后自动续期（毫秒）
```

**Redis 有状态模式：**

```properties
codingapi.security.redis.enable=true
codingapi.security.redis.valid-time=900000
codingapi.security.redis.rest-time=600000
```

### 3. 配置安全策略

```properties
# 需要认证的 URL 模式（逗号分隔）
codingapi.security.authenticated-urls=/api/**

# 免认证 URL 模式
codingapi.security.ignore-urls=/open/**,/#/**

# 登录接口地址
codingapi.security.login-processing-url=/user/login

# 登出接口地址
codingapi.security.logout-url=/user/logout
```

### 4. 核心 API

| 类 | 说明 |
|---|---|
| `TokenGateway` | Token 策略接口，提供 `create()` 和 `parser()` 方法。框架根据配置自动注入 `JWTTokenGatewayImpl` 或 `RedisTokenGatewayImpl` |
| `Token` | Token 数据对象，包含 username、authorities、extra、expireTime、remindTime 等字段，支持 `verify()` 过期校验和 `canRestToken()` 自动续期判断 |
| `TokenContext` | 线程安全的 Token 上下文工具类。`TokenContext.current()` 获取当前登录用户的 Token；`TokenContext.pushExtra()` / `getExtra()` 传递额外业务数据 |
| `SecurityLoginHandler` | 登录扩展点接口。实现 `preHandle()` 可在认证前做自定义校验；实现 `postHandle()` 可定制登录成功响应 |
| `AuthenticationTokenFilter` | 鉴权后扩展点接口。每次请求通过 Token 验证后回调，可用于刷新用户缓存等场景 |

### 5. 自定义 UserDetailsService

框架默认提供内存用户（admin/user），生产环境需自行实现：

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库加载用户信息
    }
}
```

## 使用实例

### 示例 1：自定义登录处理器

在登录前校验验证码，登录后返回额外的用户信息：

```java
@Component
public class CustomSecurityLoginHandler implements SecurityLoginHandler {

    @Override
    public void preHandle(HttpServletRequest request, HttpServletResponse response,
                          LoginRequest loginRequest) throws Exception {
        // 校验验证码
        String captcha = loginRequest.getString("captcha");
        if (captcha == null || !captchaService.verify(captcha)) {
            throw new AuthenticationServiceException("验证码错误");
        }
    }

    @Override
    public LoginResponse postHandle(HttpServletRequest request, HttpServletResponse response,
                                    LoginRequest loginRequest, UserDetails user, Token token) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token.getToken());
        loginResponse.setUsername(token.getUsername());
        loginResponse.setAuthorities(token.getAuthorities());
        // 附加用户头像等信息
        Map<String, Object> data = new HashMap<>();
        data.put("avatar", "/static/avatar/default.png");
        loginResponse.setData(data);
        return loginResponse;
    }
}
```

### 示例 2：在业务代码中获取当前用户

```java
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping
    public SingleResponse<Map<String, Object>> getProfile() {
        // 从 SecurityContext 获取当前 Token
        Token token = TokenContext.current();
        String username = token.getUsername();
        List<String> roles = token.getAuthorities();

        // 解析 extra 中的自定义数据
        UserInfo extra = token.parseExtra(UserInfo.class);

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", username);
        profile.put("roles", roles);
        profile.put("department", extra != null ? extra.getDepartment() : null);
        return SingleResponse.of(profile);
    }
}
```

### 示例 3：鉴权后刷新用户缓存

```java
@Component
public class CacheRefreshFilter implements AuthenticationTokenFilter {

    private final UserService userService;

    public CacheRefreshFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Token token = TokenContext.current();
        // 每次认证通过后刷新用户权限缓存
        userService.refreshPermissionCache(token.getUsername());
    }
}
```

### 前端调用示例

```javascript
// 登录
const res = await fetch('/user/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'admin', password: 'admin' })
});
const { data } = await res.json();
// data.token → 保存 Token

// 携带 Token 访问受保护接口
const profile = await fetch('/api/profile', {
  headers: { 'Authorization': data.token }
});
```
