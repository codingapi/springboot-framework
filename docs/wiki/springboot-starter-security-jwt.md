springboot-starter-security-jwt 功能介绍

配置文件,默认参数即说明
```properties
# JWT密钥 需大于32位的字符串
codingapi.security.jwt-secret=codingapi.security.jwt.secretkey
# JWT AES密钥
codingapi.security.ase-key=QUNEWCQlXiYqJCNYQ1phc0FDRFgkJV4mKiQjWENaYXM=
# JWT AES IV
codingapi.security.aes-iv=QUNYRkdIQEVEUyNYQ1phcw==

# JWT 有效时间(毫秒) 15分钟有效期 1000*60*15=900000
codingapi.security.jwt-time=900000
# JWT 更换令牌时间(毫秒) 10分钟后更换令牌 1000*60*10=600000
codingapi.security.jwt-rest-time=600000

# Security 配置 请求权限拦截地址
codingapi.security.authenticated-urls=/api/**
# Security 配置 登录地址
codingapi.security.login-processing-url=/user/login
# Security 配置 登出地址
codingapi.security.logout-url=/user/logout
# Security 配置 不拦截的地址
codingapi.security.ignore-urls=/open/**
# 禁用CSRF
codingapi.security.disable-csrf=true
# 禁用CORS
codingapi.security.disable-cors=true
```

## 默认账户密码
security默认的账户密码为admin/admin，可以通过重写UserDetailsService来实现自定义账户密码
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
可以通过重写SecurityLoginHandler来实现自定义登录拦截
```java
    @Bean
    public SecurityLoginHandler securityLoginHandler(){
        return (request, response, handler) -> {
            //TODO 自定义登录拦截
        };
    }
```

## 获取当前用户

通过TokenContext获取当前用户信息
```java
    @GetMapping("/user")
    public String user(){
        return TokenContext.current().getUsername();
    }
```

可以通过Token的extra字段来存储用户的更多信息，然后通过TokenContext获取
```java
    @GetMapping("/user")
    public String user(){
        return TokenContext.current().getExtra("user");
    }
```
