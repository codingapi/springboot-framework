---
name: context-singleton
description: 全局状态通过单例 Context 类持有，由 Register 类在启动时注入依赖
status: 已实现
scope: 后端
source: 项目自有
last_commit: 303b377f
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/properties/PropertiesContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/exception/MessageContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContextRegister.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/user/UserContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/RestTemplateContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/DomainEventContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/trigger/TriggerContext.java
  - springboot-starter-security/src/main/java/com/codingapi/springboot/security/gateway/TokenContext.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/interceptor/SQLInterceptorContext.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/DataAuthorizationContext.java
---

## 解决什么问题

框架层工具类（如事件推送、事务管理、异常国际化）需要在非 Spring 管理的上下文中使用（例如 Groovy 脚本运行时、静态方法调用、线程池异步任务）。如果直接依赖 Spring Bean 注入，在这些场景中将无法获取实例。

单例 Context 模式解决了这个问题：通过静态单例持有全局状态，由 Spring 管理的 `*Register` 类在应用启动时注入所需依赖，使得框架能力在任何地方都能通过 `XxxContext.getInstance()` 访问。

## 如何使用

### 命名规范

| 类后缀 | 职责 | 示例 |
|--------|------|------|
| `*Context` | 持有全局状态的单例类 | `PropertiesContext`, `UserContext`, `TransactionManagerContext` |
| `*Register` | `InitializingBean` 实现，启动时注入依赖到 Context | `TransactionManagerContextRegister` |
| `*Configuration` | `@Configuration` 类，注册 Context Bean 和 Register Bean | `TransactionManagerContextConfiguration` |

### Context 类结构

```java
public class XxxContext {
    // 饿汉式单例（推荐）或懒汉式（需双重检查锁）
    @Getter
    private static final XxxContext instance = new XxxContext();

    private XxxContext() {}

    // 注入的依赖（由 Register 设置）
    private SomeDependency dependency;

    public void setDependency(SomeDependency dependency) {
        this.dependency = dependency;
    }

    // 对外 API
    public void doSomething() {
        dependency.execute();
    }
}
```

### Register 类结构

```java
public class XxxContextRegister implements InitializingBean {
    private final SomeDependency dependency;

    public XxxContextRegister(SomeDependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public void afterPropertiesSet() {
        XxxContext.getInstance().setDependency(dependency);
    }
}
```

### 已注册的 Context 类

| Context | 持有状态 | 用途 |
|---------|---------|------|
| `PropertiesContext` | `FrameworkProperties` | 框架配置属性 |
| `MessageContext` | `MessageSource` | 国际化消息 |
| `TransactionManagerContext` | `PlatformTransactionManager` | 编程式事务 |
| `UserContext` | `ThreadLocal<IUser>` | 当前登录用户 |
| `RestTemplateContext` | `RestTemplate` | HTTP 客户端 |
| `DomainEventContext` | 领域事件上下文 | 事件变更追踪 |
| `TokenContext` | `ThreadLocal<String>` | 当前 Token 扩展信息 |
| `SQLInterceptorContext` | SQL 拦截状态 | 数据权限上下文 |
| `TriggerContext` | 触发器注册表 | 触发器管理 |

## 使用实例

```java
// ✅ 正确 — 通过 Context 单例访问
public class GroovyScriptRuntime {
    public Object execute(String script) {
        // 在 Groovy 脚本中使用框架事务
        return TransactionManagerContext.getInstance().commit(() -> {
            return runScript(script);
        });
    }
}

// ✅ 正确 — Register 在启动时注入
@Bean
public TransactionManagerContextRegister transactionManagerContextRegister(
        PlatformTransactionManager transactionManager) {
    return new TransactionManagerContextRegister(transactionManager);
}

// ❌ 错误 — 在非 Spring 管理的类中直接注入 Bean
public class GroovyScriptRuntime {
    @Autowired // 无法注入！此类不是 Spring Bean
    private PlatformTransactionManager transactionManager;
}

// ❌ 错误 — 在静态工具类中使用 ThreadLocal 之外的 Spring Bean
public class EventPusher {
    @Autowired // 静态上下文无法注入
    private static ApplicationEventPublisher publisher;
}
```
