springboot-starter-security 功能介绍

支持无状态的JWT和有状态的redis两种不同的token机制

配置文件及默认参数说明
```properties
# JWT开关，必须显式配置为true才会启用JWT认证（默认不开启）
codingapi.security.jwt.enable=true
# JWT密钥 需大于32位的字符串
codingapi.security.jwt.secret-key=codingapi.security.jwt.secretkey

# JWT 有效时间(毫秒) 15分钟有效期 1000*60*15=900000
codingapi.security.jwt.valid-time=900000
# JWT 更换令牌时间(毫秒) 10分钟后更换令牌 1000*60*10=600000
codingapi.security.jwt.rest-time=600000

# JWT AES密钥
codingapi.security.ase-key=QUNEWCQlXiYqJCNYQ1phc0FDRFgkJV4mKiQjWENaYXM=
# JWT AES IV
codingapi.security.ase-iv=QUNYRkdIQEVEUyNYQ1phcw==

# Redis开关，必须显式配置为true才会启用Redis有状态认证（默认不开启）
#codingapi.security.redis.enable=true
# Redis token 有效时间(毫秒)，默认15分钟 1000*60*15=900000
#codingapi.security.redis.valid-time=900000
# Redis 更换令牌时间(毫秒)，默认10分钟后更换令牌 1000*60*10=600000
#codingapi.security.redis.rest-time=600000
#spring.data.redis.host=localhost
#spring.data.redis.port=6379

# Security 配置 请求权限拦截地址
codingapi.security.authenticated-urls=/api/**
# Security 配置 登录地址
codingapi.security.login-processing-url=/user/login
# Security 配置 登出地址
codingapi.security.logout-url=/user/logout
# Security 配置 不拦截的地址
codingapi.security.ignore-urls=/open/**
# 禁用CSRF（默认true）
codingapi.security.disable-csrf=true
# 禁用CORS（默认true）
codingapi.security.disable-cors=true
# 禁用Basic Auth（默认true）
codingapi.security.disable-basic-auth=true
# 禁用FrameOptions（默认true）
codingapi.security.disable-frame-options=true
```

> **注意**：JWT 与 Redis 两种认证模式均通过 `@ConditionalOnProperty(havingValue = "true")` 条件装配，且未设置 `matchIfMissing`，因此**必须显式配置** `codingapi.security.jwt.enable=true` 或 `codingapi.security.redis.enable=true` 才会启用对应的认证方式，默认均不开启。

## 默认账户密码
security默认创建了两个账户（见 `AutoConfiguration.userDetailsService()`，仅在未自定义 `UserDetailsService` 时生效）：

| 账户 | 密码 | 角色 |
|------|------|------|
| admin | admin | ADMIN |
| user | admin | USER |

可以通过重写UserDetailsService来实现自定义账户密码
```java
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        String password = passwordEncoder.encode("12345678");

        UserDetails admin = User.withUsername("admin")
                .password(password)
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(password)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
```
也可以通过数据库账户获取账户数据，请自己实现UserDetailsService接口

## 登录拦截
可以通过重写SecurityLoginHandler来实现自定义登录拦截，preHandle登录前的拦截处理，postHandle登录后的拦截处理（返回值为登录响应对象）。

> 说明：Spring Boot 3 使用 `jakarta.servlet` 命名空间，以下 import 为 `jakarta.servlet.http.*`；Spring Boot 2（框架 8.2.x 及以下版本）需替换为 `javax.servlet.http.*`。

```java
import com.codingapi.springboot.security.dto.request.LoginRequest;
import com.codingapi.springboot.security.dto.response.LoginResponse;
import com.codingapi.springboot.security.filter.SecurityLoginHandler;
import com.codingapi.springboot.security.gateway.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityLoginHandler securityLoginHandler() {
        return new SecurityLoginHandler() {
            @Override
            public void preHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest) throws Exception {

            }

            @Override
            public LoginResponse postHandle(HttpServletRequest request, HttpServletResponse response,
                                            LoginRequest loginRequest, UserDetails user, Token token) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setToken(token.getToken());
                loginResponse.setUsername(token.getUsername());
                loginResponse.setAuthorities(token.getAuthorities());
                return loginResponse;
            }
        };
    }
}
```

以上 `postHandle` 即框架默认实现（见 `AutoConfiguration.securityLoginHandler()`），自定义时可在此基础上补充业务数据（通过 `loginResponse.setData(...)` 返回）。

## 获取当前用户

通过TokenContext获取当前用户信息
```java
    @GetMapping("/user")
    public String user(){
        return TokenContext.current().getUsername();
    }
```

可以通过Token的extra字段来存储用户的更多信息（extra 存储的是 JSON 字符串），然后通过TokenContext获取：

- `TokenContext.current().getExtra()` 获取原始的 extra JSON 字符串
- `TokenContext.current().parseExtra(UserInfo.class)` 将 extra JSON 字符串反序列化为指定类型的对象

```java
    @GetMapping("/user")
    public String user(){
        // 获取原始的 extra JSON 字符串
        return TokenContext.current().getExtra();
    }

    @GetMapping("/user/info")
    public UserInfo userInfo(){
        // 将 extra JSON 字符串反序列化为 UserInfo 对象
        return TokenContext.current().parseExtra(UserInfo.class);
    }
```
