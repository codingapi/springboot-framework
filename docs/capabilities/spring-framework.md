---
name: spring-framework
description: Spring Framework / Spring Boot 核心能力 — IoC 容器、自动配置、事件发布、AOP、Web MVC
status: 已实现
scope: 后端
source: 框架:Spring Boot
import: org.springframework.boot:spring-boot-starter
framework_version: 3.3.5
---

## 解决什么问题

Spring Boot 作为项目的基础框架，提供以下核心能力：

- **IoC 容器**：依赖注入（DI）管理所有 Bean 的生命周期
- **自动配置**：`@SpringBootApplication` 自动装配 Starter 组件
- **事件发布**：`ApplicationEventPublisher` 发布 Spring 原生事件
- **AOP 切面**：声明式事务（`@Transactional`）、日志切面等
- **Web MVC**：REST 控制器、参数绑定、拦截器、异常处理
- **条件装配**：`@ConditionalOnClass` / `@ConditionalOnProperty` 按需加载

## 如何使用

### 自动配置注册

各 Starter 模块通过以下文件注册自动配置：
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 3.x）
- `META-INF/spring.factories`（兼容旧版）

### Spring 事件集成

本框架的事件系统底层桥接了 Spring 的 `ApplicationEventPublisher`：
```java
// DomainEventContext 内部使用 Spring 事件发布
context.publishEvent(new DomainEvent(event, sync, traceId));
```

### 配置属性

```properties
# Spring Boot 基础配置
server.port=8090
spring.application.name=my-app
```

## 使用实例

```java
// 自定义自动配置
@Configuration
@ConditionalOnClass(name = "org.springframework.web.servlet.HandlerExceptionResolver")
public class BasicHandlerExceptionResolverConfiguration {
    @Bean
    public HandlerExceptionResolver servletExceptionHandler() {
        return new ServletExceptionHandler();
    }
}

// 声明式事务
@Transactional
public void createUser(User user) {
    userRepository.save(user);
    EventPusher.push(new UserCreatedEvent(user.getId()));
}
```
