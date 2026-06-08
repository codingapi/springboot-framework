---
name: springboot/security
module: springboot
description: Spring Security，提供认证、授权和 CSRF 防护
status: 已实现
scope: 后端
source: 框架:springboot
import: "org.springframework.boot:spring-boot-starter-security"
framework_version: 3.3.5
---

## 解决什么问题

在企业级 Web 应用中，安全是最基础且最复杂的横切关注点。开发者通常需要面对以下痛点：

- **认证机制繁琐**：手动实现 JWT / Redis Token 的签发、解析、续期和过期校验逻辑，代码重复且容易出错。
- **授权规则分散**：URL 级别的访问控制散落在各个 Controller 或拦截器中，缺乏统一的安全策略配置入口。
- **登录/登出流程耦合**：JSON API 场景下的登录成功/失败响应、Token 返回格式、登出清理等逻辑与业务代码高度耦合。
- **安全防护遗漏**：CSRF、CORS、Frame Options 等浏览器安全头需要逐一配置，默认值往往不适合前后端分离架构。
- **扩展困难**：当需要在认证前后插入自定义逻辑（如验证码校验、审计日志、多因子认证）时，缺少标准化的扩展点。

`springboot-starter-security` 模块在 Spring Security 6.x 基础上进行了二次封装，通过 `TokenGateway` 抽象令牌存储（JWT / Redis 双模式）、`SecurityLoginHandler` 开放登录生命周期钩子、`HttpSecurityCustomer` 允许自定义安全链配置，将上述问题收敛到统一的自动配置体系中，使业务项目只需声明少量配置即可获得生产级的安全基础设施。

## 如何使用

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-security</artifactId>
</dependency>
```

该 starter 会自动引入 `spring-boot-starter-security` 并注册 `AutoConfiguration`。

### 2. 核心配置项

在 `application.properties` 中通过 `codingapi.security.*` 前缀进行配置：

```properties
# 需要认证的 URL 模式（逗号分隔），匹配后必须携带有效 Token
codingapi.security.authenticated-urls=/api/**

# 免认证的 URL 模式（逗号分隔）
codingapi.security.ignore-urls=/open/**,/public/**

# 登录接口地址（POST）
codingapi.security.login-processing-url=/user/login

# 登出接口地址
codingapi.security.logout-url=/user/logout

# 禁用 CSRF（前后端分离场景通常为 true）
codingapi.security.disable-csrf=true

# 禁用 CORS 内置处理（由框架自动配置 CORS 映射）
codingapi.security.disable-cors=true

# 禁用 Basic Auth
codingapi.security.disable-basic-auth=true

# 禁用 Frame Options
codingapi.security.disable-frame-options=true
```

### 3. 核心接口

| 接口 | 职责 | 扩展方式 |
|------|------|----------|
| `TokenGateway` | 令牌的创建（`create`）与解析（`parser`） | 实现该接口或使用内置的 `JWTTokenGatewayImpl` / `RedisTokenGatewayImpl` |
| `SecurityLoginHandler` | 登录前置校验（`preHandle`）与后置响应构建（`postHandle`） | 实现该接口并注册为 Bean，替换默认行为 |
| `AuthenticationTokenFilter` | Token 验证通过后的附加过滤逻辑 | 实现该接口并注册为 Bean |
| `HttpSecurityCustomer` | 自定义 `HttpSecurity` 配置链 | 实现 `customize(HttpSecurity)` 方法 |
| `UserDetailsService` | 用户加载 | 注册自定义 Bean 替换默认的内存用户 |
| `PasswordEncoder` | 密码编码 | 注册自定义 Bean 替换默认的 DelegatingPasswordEncoder |

所有接口均标注 `@ConditionalOnMissingBean`，业务项目注册同名 Bean 即可无缝替换默认实现。

### 4. Token 模型

`Token` 对象封装了完整的令牌信息：

- `username` — 用户名
- `authorities` — 权限列表
- `extra` — 业务扩展字段（JSON 字符串，可通过 `parseExtra(Class)` 反序列化）
- `expireTime` / `remindTime` — 过期时间与续期提醒时间
- `canRestToken()` — 判断是否需要自动续期
- `getAuthenticationToken()` — 转换为 Spring Security 的 `UsernamePasswordAuthenticationToken`

### 5. 自动装配链路

`AutoConfiguration` 自动完成以下装配：

1. 注册 `SecurityFilterChain`，配置 URL 授权规则、异常处理、登录/登出端点
2. 通过 `HttpSecurityConfigurer` 注入 `MyLoginFilter`（登录）和 `MyAuthenticationFilter`（Token 鉴权）
3. 注册 `DaoAuthenticationProvider` 绑定 `UserDetailsService` 和 `PasswordEncoder`
4. 根据 `disableCors` 配置自动注册 CORS 映射

## 使用实例

### 示例 1：自定义 UserDetailsService 对接数据库

```java
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseUserDetailsService(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        return User.withUsername(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRole())
                .build();
    }
}
```

注册此 Bean 后，框架自动替换默认的内存用户管理器。

### 示例 2：自定义登录处理器（增加验证码校验）

```java
@Component
public class CaptchaSecurityLoginHandler implements SecurityLoginHandler {

    private final CaptchaService captchaService;

    public CaptchaSecurityLoginHandler(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @Override
    public void preHandle(HttpServletRequest request, HttpServletResponse response,
                          LoginRequest loginRequest) throws Exception {
        String captchaCode = loginRequest.getString("captcha");
        String captchaKey = loginRequest.getString("captchaKey");
        if (!captchaService.verify(captchaKey, captchaCode)) {
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
        // 可在此处追加额外的响应字段
        return loginResponse;
    }
}
```

### 示例 3：Token 验证后注入上下文

```java
@Component
public class TenantAuthenticationTokenFilter implements AuthenticationTokenFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Token token) {
            // 从 Token extra 中提取租户 ID 并放入线程上下文
            TenantContext context = token.parseExtra(TenantContext.class);
            if (context != null) {
                TenantHolder.set(context.getTenantId());
            }
        }
    }
}
```

### 示例 4：自定义 HttpSecurity 配置

```java
@Component
public class CustomHttpSecurityCustomer implements HttpSecurityCustomer {

    @Override
    public void customize(HttpSecurity security) throws Exception {
        // 启用 OAuth2 Resource Server
        security.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        // 添加自定义 Header
        security.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")));
    }
}
```

### 示例 5：前端调用登录接口

```bash
curl -X POST http://localhost:8090/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

成功响应：

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "authorities": ["ROLE_ADMIN"]
  }
}
```

后续请求携带 Token：

```bash
curl http://localhost:8090/api/users \
  -H "Authorization: eyJhbGciOiJIUzI1NiJ9..."
```
