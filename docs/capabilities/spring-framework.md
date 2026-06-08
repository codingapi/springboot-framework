---
name: spring-framework
description: Spring Framework / Spring Boot 核心基础能力，提供 IoC 容器、事件发布、AOP、Web MVC、事务管理等基础设施
status: 已实现
scope: 后端
source: 框架:spring-framework
framework_version: 3.3.5
---

## 解决什么问题

Spring Framework / Spring Boot 是整个框架的运行时基座，解决了以下核心问题：

- **IoC 容器**：管理所有 Bean 的生命周期与依赖注入，框架中所有 Starter 模块的自动配置均基于 Spring Boot AutoConfiguration 机制（`spring.factories` + `AutoConfiguration.imports`）
- **事件发布（ApplicationEventPublisher）**：框架自建的事件系统（`DomainEvent`、`IHandler`）底层通过 Spring 的 `@EventListener` 桥接，由 `SpringDefaultEventHandler` / `SpringTransactionEventHandler` 订阅 `DomainEvent` 并分发到业务 Handler
- **AOP**：支持声明式事务（`@Transactional`）、数据权限 SQL 拦截等切面能力
- **Web MVC**：统一 REST API 响应封装（`Response` / `SingleResponse` / `MultiResponse`）、全局异常处理、参数绑定
- **事务管理**：工作流引擎、领域事件持久化等场景均依赖 Spring 声明式事务

## 如何使用

### 依赖引入

框架根 POM 继承自 `spring-boot-starter-parent:3.3.5`，所有子模块自动获得 Spring Boot 依赖管理。

```xml
<!-- 核心 starter（已包含 spring-boot-starter） -->
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter</artifactId>
</dependency>
```

### 事件系统桥接

框架通过 `SpringEventConfiguration` 注册 `SpringEventInitializer`，在容器启动时将 `ApplicationContext` 注入 `DomainEventContext`，使 `EventPusher.push()` 能够发布 Spring `DomainEvent`：

```java
// 框架内部自动配置，无需手动操作
@Bean
public SpringEventInitializer springEventInitializer(ApplicationContext context) {
    return new SpringEventInitializer(context);
}
```

业务侧只需实现 `IHandler<T>` 接口即可订阅事件，Handler 由 `HandlerBeanDefinitionRegistrar` 自动扫描注册。

### 事务管理

使用标准 Spring `@Transactional` 注解：

```java
@Transactional
public void startFlow(...) { ... }
```

可通过 `codingapi.framework.event.transaction.enable=true` 启用事务内事件处理模式（`SpringTransactionEventHandler`），确保事件在事务提交后触发。

## 使用实例

### 定义并订阅领域事件

```java
// 1. 定义事件
public class OrderCreatedEvent implements ISyncEvent {
    private final String orderId;
    // constructor, getters...
}

// 2. 实现处理器（自动注册为 Spring Bean）
@Component
public class OrderCreatedHandler implements IHandler<OrderCreatedEvent> {
    @Override
    public int order() { return 0; }

    @Override
    public void handler(OrderCreatedEvent event) {
        // 处理订单创建逻辑
    }

    @Override
    public void error(OrderCreatedEvent event, Exception exception) {
        // 异常回调
    }
}

// 3. 发布事件
EventPusher.push(new OrderCreatedEvent("ORD-001"), true);
```

### 统一响应封装

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public SingleResponse<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return SingleResponse.of(user);
    }

    @GetMapping
    public MultiResponse<UserDTO> listUsers(PageRequest request) {
        Page<UserDTO> page = userService.findAll(request);
        return MultiResponse.of(page.getContent(), page.getTotalElements());
    }
}
```
