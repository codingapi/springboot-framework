---
name: springboot/spring-boot
module: springboot
description: Spring Boot 2.7 基础框架，提供 IoC 容器、AOP、事件发布、配置自动装配等核心能力
status: 已实现
scope: 后端
source: 框架:springboot
import: "org.springframework.boot:spring-boot-starter-parent"
framework_version: 2.7.18
---

## 解决什么问题

springboot-framework 整体运行在 Spring Boot 容器之上。本能力为上层各 starter 模块提供：

- **IoC 容器**（`ApplicationContext`）：管理 `IHandler`/`RowHandler`/`ColumnHandler` 等扩展点的自动注入
- **AOP**（`spring-aop` + `cglib`）：支撑 `DomainChangeInterceptor`（CGLIB 动态代理）和 `DomainProxyFactory`（实体的方法拦截）
- **事件发布**（`ApplicationEventPublisher`）：本框架的领域事件（`DomainEvent`）直接继承自 `org.springframework.context.ApplicationEvent`
- **配置自动装配**（`@Configuration` + `spring.factories` / `AutoConfiguration.imports`）：每个 starter 模块通过 `AutoConfiguration` 类自动注册 Bean
- **事务管理**（`@Transactional` + `TransactionPhase.AFTER_COMMIT`）：支撑 `SpringTransactionEventHandler` 在事务提交后再触发 handler
- **JDBC 数据源**（`DataSource`）：支撑 `springboot-starter-data-authorization` 的自定义 JDBC 驱动

## 如何使用

**1. 引入依赖**

所有 starter 模块的 `pom.xml` 都已继承 `spring-boot-starter-parent:2.7.18`，业务方无需重复声明版本：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>
```

**2. 自动装配入口**

每个 starter 模块提供 `AutoConfiguration` 类，被 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册：

```
com.codingapi.springboot.framework.AutoConfiguration
com.codingapi.springboot.script.AutoConfiguration
com.codingapi.springboot.security.AutoConfiguration
com.codingapi.springboot.fast.DataFastConfiguration
com.codingapi.springboot.flow.FlowConfiguration
com.codingapi.springboot.authorization.DataAuthorizationConfiguration
```

**3. 常用扩展点（本框架对 Spring Boot 的封装）**

| Spring 抽象 | 本框架封装 | 作用 |
|------------|-----------|------|
| `ApplicationContext.publishEvent()` | `EventPusher.push(IEvent)` | 同步/异步领域事件分发 |
| `@EventListener` | `IHandler<T>` | 类型安全的事件处理 |
| `BeanPostProcessor` | `HandlerBeanDefinitionRegistrar` | 扫描 `Handler` 注解自动注册 |
| `JdbcTemplate` / `DataSource` | `AuthorizationJdbcDriver` | 数据权限驱动 |
| `TransactionSynchronization` | `SpringTransactionEventHandler` | 事务后事件 |

**4. 配置项前缀**

| 前缀 | 含义 | 模块 |
|------|------|------|
| `codingapi.framework.*` | 框架主配置（线程池、事务事件开关） | springboot-starter |
| `codingapi.script.*` | 脚本引擎配置（缓存大小、临时脚本） | springboot-starter-script |
| `codingapi.security.*` | 安全/Token 配置 | springboot-starter-security |
| `codingapi.authorization.*` | 数据权限配置 | springboot-starter-data-authorization |

**5. 与 starter 模块的关系**

```
spring-boot-starter (基础)
    ├── springboot-starter (DDD 领域框架)
    │   ├── springboot-starter-script
    │   │   ├── springboot-starter-data-fast
    │   │   └── springboot-starter-flow
    │   ├── springboot-starter-security
    │   └── springboot-starter-data-authorization
```

业务方按需引入 starter 即可获得对应能力，无需关心 Spring Boot 底层细节。

## 使用实例

```java
// 1. 业务方应用：仅引入需要的 starter
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

// 2. application.yml
spring:
  application:
    name: my-order-service
  datasource:
    url: jdbc:mysql://localhost:3306/order
  jpa:
    hibernate:
      ddl-auto: update

codingapi:
  framework:
    event:
      transaction:
        enable: true
  authorization:
    show-sql: true
```

```xml
<!-- pom.xml 业务方应用 -->
<dependencies>
    <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-data-authorization</artifactId>
    </dependency>
</dependencies>
```
