---
name: context-register-pairing
description: 每个 Context 单例必须配套 Register 类和 Configuration 类，形成三件套：Context（持有状态）+ Register（注入依赖）+ Configuration（注册 Bean）
status: 已实现
scope: 后端
source: 项目自有
last_commit: 303b377f
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContextRegister.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContextConfiguration.java
---

## 解决什么问题

本规范是对已有 `context-singleton` 规范的补充细化。`context-singleton` 定义了 Context 单例的基本结构，但在实际开发中经常出现以下问题：

1. **Register 缺失**：开发者创建了 Context 单例，但忘记编写对应的 Register 类来注入 Spring Bean 依赖，导致 Context 中的依赖为 null，运行时才暴露 NPE。
2. **Configuration 遗漏**：Register 类虽然写了，但没有通过 `@Configuration` + `@Bean` 注册到 Spring 容器中，Register 不会被实例化，`afterPropertiesSet()` 永远不会被调用。
3. **三者分散在不同模块**：Context、Register、Configuration 散落在不同的包或模块中，新增功能时难以找到完整的配对关系，维护成本高。
4. **依赖注入方式不一致**：有的用构造函数注入，有的用 `@Autowired` 字段注入，有的直接在 Configuration 中调用 setter，缺乏统一模式。

本规范要求：**每创建一个 Context 单例，必须同时创建配套的 Register 和 Configuration 类，三者缺一不可，且放在同一个包下。**

## 如何使用

### 三件套命名规范

以 `Xxx` 为业务前缀，三个类的命名固定为：

| 类名 | 职责 | 关键特征 |
|------|------|---------|
| `XxxContext` | 持有全局状态的饿汉式单例 | `private static final XxxContext instance = new XxxContext()`；私有构造函数；提供 `getInstance()` |
| `XxxContextRegister` | 实现 `InitializingBean`，在 Bean 初始化完成后将依赖注入到 Context | 构造函数接收所需依赖；`afterPropertiesSet()` 中调用 Context 的 setter |
| `XxxContextConfiguration` | `@Configuration` 类，通过 `@Bean` 方法注册 Register 实例 | 可声明 `@ConditionalOnClass` 等条件注解；依赖参数使用 `@Autowired(required = false)` 避免启动失败 |

### 文件组织

三个类必须放在**同一个 Java 包**下，文件名与类名一致：

```
com.codingapi.springboot.framework.transaction/
├── TransactionManagerContext.java
├── TransactionManagerContextRegister.java
└── TransactionManagerContextConfiguration.java
```

### Context 类模板

```java
@Slf4j
public class XxxContext {

    @Getter
    private static final XxxContext instance = new XxxContext();

    private SomeDependency dependency;

    private XxxContext() {}

    public void setDependency(SomeDependency dependency) {
        this.dependency = dependency;
        if (dependency != null) {
            log.info("{} load success", dependency);
        }
    }

    // 对外 API 方法
    public <T> T execute(Supplier<T> supplier) {
        if (dependency != null) {
            // 使用依赖执行业务逻辑
        }
        return supplier.get();
    }
}
```

### Register 类模板

```java
public class XxxContextRegister implements InitializingBean {

    private final SomeDependency dependency;

    public XxxContextRegister(SomeDependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        XxxContext.getInstance().setDependency(dependency);
    }
}
```

### Configuration 类模板

```java
@Configuration
public class XxxContextConfiguration {

    @Bean
    public XxxContextRegister xxxContextRegister(
            @Autowired(required = false) SomeDependency dependency) {
        return new XxxContextRegister(dependency);
    }
}
```

### 关键规则

1. **Register 只负责注入**：`afterPropertiesSet()` 中只做 setter 调用，不包含业务逻辑。
2. **Configuration 只负责注册**：`@Bean` 方法只构造 Register 实例，不做其他操作。
3. **可选依赖使用 `required = false`**：当依赖可能不存在时（如 `PlatformTransactionManager` 在某些测试环境中），使用 `@Autowired(required = false)` 避免启动失败。Context 的 API 方法内部需对 null 做防御性检查。
4. **Context 的 setter 应有日志**：注入成功时打印 INFO 日志，便于排查启动问题。
5. **自动配置注册**：Configuration 类需在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 和 `META-INF/spring.factories` 中同时注册。

## 使用实例

```java
// ✅ 正确 — 完整的三件套（以 TransactionManager 为例）

// 1. Context：持有 PlatformTransactionManager，提供编程式事务 API
public class TransactionManagerContext {
    @Getter
    private static final TransactionManagerContext instance = new TransactionManagerContext();
    @Getter
    private PlatformTransactionManager platformTransactionManager;

    private TransactionManagerContext() {}

    public void setPlatformTransactionManager(PlatformTransactionManager ptm) {
        this.platformTransactionManager = ptm;
        if (ptm != null) {
            log.info("platformTransactionManager:{} load success", ptm);
        }
    }

    public <T> T commit(Supplier<T> supplier) {
        if (platformTransactionManager != null) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = platformTransactionManager.getTransaction(def);
            try {
                T result = supplier.get();
                platformTransactionManager.commit(status);
                return result;
            } catch (Exception e) {
                platformTransactionManager.rollback(status);
                throw e;
            }
        }
        return supplier.get(); // 无事务管理器时降级执行
    }
}

// 2. Register：启动时将 TransactionManager 注入到 Context
public class TransactionManagerContextRegister implements InitializingBean {
    private final PlatformTransactionManager transactionManager;

    public TransactionManagerContextRegister(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void afterPropertiesSet() {
        TransactionManagerContext.getInstance()
            .setPlatformTransactionManager(transactionManager);
    }
}

// 3. Configuration：注册 Register Bean
@Configuration
public class TransactionManagerContextConfiguration {
    @Bean
    public TransactionManagerContextRegister transactionManagerContextRegister(
            @Autowired(required = false) PlatformTransactionManager ptm) {
        return new TransactionManagerContextRegister(ptm);
    }
}

// ✅ 正确 — 在非 Spring 管理的代码中使用 Context
public class GroovyScriptRuntime {
    public <T> T invoke(String method, String script, TransactionMode mode) {
        if (mode == TransactionMode.COMMIT) {
            return TransactionManagerContext.getInstance().commit(() -> {
                return (T) runtime.invokeMethod(method, args);
            });
        }
        // ...
    }
}

// ❌ 错误 — 只有 Context，缺少 Register 和 Configuration
public class CacheContext {
    @Getter
    private static final CacheContext instance = new CacheContext();
    private CacheManager cacheManager;

    private CacheContext() {}

    // 没有 Register 注入 cacheManager，永远为 null！
    public void put(String key, Object value) {
        cacheManager.put(key, value); // NPE!
    }
}

// ❌ 错误 — Register 存在但没有 Configuration 注册
// TransactionManagerContextRegister 写了，但没有 @Configuration 类将其注册为 Bean
// afterPropertiesSet() 永远不会被调用

// ❌ 错误 — 在 Configuration 中直接操作 Context，跳过 Register
@Configuration
public class BadConfiguration {
    @Bean
    public SomeBean someBean(PlatformTransactionManager ptm) {
        // 不应在普通 Bean 定义中直接操作 Context
        TransactionManagerContext.getInstance().setPlatformTransactionManager(ptm);
        return new SomeBean();
    }
}
```
