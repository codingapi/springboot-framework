---
name: spring-security
description: Spring Security 认证与授权框架 — 提供 Filter 链、CSRF 防护、密码编码、权限控制
status: 已实现
scope: 后端
source: 框架:Spring Security
import: org.springframework.boot:spring-boot-starter-security
framework_version: (由 Spring Boot 3.3.5 管理)
---

## 解决什么问题

Spring Security 为 Web 应用提供完整的安全基础设施：

- **Filter 链**：请求级别的认证与授权过滤
- **CSRF 防护**：跨站请求伪造保护（可通过配置关闭）
- **密码编码**：`PasswordEncoder` 安全存储密码
- **会话管理**：有状态（Session）与无状态（JWT）两种模式
- **权限注解**：`@PreAuthorize` / `@Secured` 方法级权限控制

## 如何使用

### 与本框架集成

本框架的 `springboot-starter-security` 模块已封装 Spring Security 配置：

```java
// HttpSecurityConfigurer — 自定义 Security 配置
public interface HttpSecurityCustomer {
    void customer(HttpSecurity http) throws Exception;
}

// 注入自定义配置
@Bean
public HttpSecurityCustomer customSecurity() {
    return http -> http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
    );
}
```

### 免认证 URL

```properties
codingapi.security.ignore-urls=/open/**,/#/**,/api/version
```

### 权限注解

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { ... }
```

## 使用实例

```java
// 自定义 UserDetailsService
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userService.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), user.getPassword(),
            user.getAuthorities()
        );
    }
}
```
