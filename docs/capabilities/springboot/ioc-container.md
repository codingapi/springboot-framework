---
name: springboot/ioc-container
module: springboot
description: Spring IoC 容器，提供依赖注入、AOP、事件发布等核心功能
status: 已实现
scope: 后端
source: 框架:springboot
import: "org.springframework.boot:spring-boot-starter"
framework_version: 3.3.5
---

## 解决什么问题

在企业级 Java 应用开发中，对象之间的依赖关系往往错综复杂。如果由开发者手动创建和管理所有对象及其依赖，会导致代码高度耦合、难以测试、难以维护。Spring IoC（Inversion of Control）容器通过以下机制解决这些核心痛点：

- **依赖注入（DI）**：将对象的创建和组装交由容器统一管理，业务代码无需关心依赖的来源与生命周期，从而实现组件间的解耦。
- **声明式配置**：通过注解或 Java Config 描述 Bean 的定义与装配规则，替代冗长的工厂模式或手动 new 对象的方式。
- **AOP 支持**：在不修改业务代码的前提下，以切面方式织入事务管理、日志记录、权限校验等横切关注点。
- **事件驱动**：提供 `ApplicationEventPublisher` 发布-订阅机制，使模块间可以通过事件进行松耦合通信，这也是本项目 DDD 领域事件基础设施的底层支撑。
- **生命周期管理**：容器负责 Bean 的初始化、回调（如 `@PostConstruct`）和销毁，简化资源管理逻辑。

典型复用场景包括：Service 层注入 Repository、Controller 注入 Service、通过 AOP 统一处理异常与日志、使用 `ApplicationListener` 监听启动完成事件等。

## 如何使用

### 引入依赖

在 Maven 项目的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

该 starter 已包含 `spring-context`、`spring-aop`、`spring-beans` 等 IoC 核心模块。在本项目中，`springboot-starter` 及其他所有 starter 均已传递依赖此包，无需额外声明。

### Bean 定义与注入

| 方式 | 说明 |
|------|------|
| `@Component` / `@Service` / `@Repository` / `@Controller` | 类级别注解，标记为 Spring 管理的 Bean |
| `@Bean` | 在 `@Configuration` 类的方法上声明第三方或非注解类的 Bean |
| `@Autowired` | 按类型自动注入（推荐用于构造器注入） |
| `@Qualifier("beanName")` | 当同类型存在多个 Bean 时，指定具体名称 |
| `@Value("${key}")` | 注入配置文件中的属性值 |
| `@Scope("prototype")` | 改变 Bean 作用域，默认为 `singleton` |

### AOP 使用

1. 添加 `spring-boot-starter-aop` 依赖（或使用 AspectJ）。
2. 编写切面类，使用 `@Aspect` + `@Component` 标注。
3. 通过 `@Around`、`@Before`、`@After` 等通知注解定义切入点与增强逻辑。

### 事件发布与监听

- 注入 `ApplicationEventPublisher`，调用 `publishEvent()` 发布自定义事件。
- 使用 `@EventListener` 注解或实现 `ApplicationListener<T>` 接口监听事件。
- 配合 `@TransactionalEventListener` 可在事务提交后再触发处理逻辑。

### 配置方式

Spring Boot 优先使用基于注解的配置风格：

- `@SpringBootApplication` 组合了 `@Configuration`、`@EnableAutoConfiguration`、`@ComponentScan`。
- `@Import` 可导入额外的配置类。
- `application.properties` / `application.yml` 提供外部化配置，通过 `@ConfigurationProperties` 绑定到 Bean。

## 使用实例

### 基本依赖注入

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    // 构造器注入（推荐方式，Spring 4.3+ 单构造器可省略 @Autowired）
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public SingleResponse<User> getUser(@PathVariable Long id) {
        return Response.ok(userService.findById(id));
    }
}
```

### 通过 @Bean 注册第三方组件

```java
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}
```

### AOP 切面示例

```java
@Aspect
@Component
@Slf4j
public class MethodExecutionLogAspect {

    @Around("@annotation(org.springframework.stereotype.Service)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            log.info("{} executed in {}ms", methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("{} failed after {}ms", methodName, System.currentTimeMillis() - start, ex);
            throw ex;
        }
    }
}
```

### 事件发布与监听

```java
// 定义事件
public class UserCreatedEvent extends ApplicationEvent {
    private final User user;

    public UserCreatedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

// 发布事件
@Service
public class UserService {

    private final ApplicationEventPublisher eventPublisher;

    public UserService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public User createUser(CreateUserCommand command) {
        User user = new User(command.getName(), command.getEmail());
        userRepository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(this, user));
        return user;
    }
}

// 监听事件
@Component
@Slf4j
public class UserCreatedNotificationHandler {

    @EventListener
    public void onUserCreated(UserCreatedEvent event) {
        log.info("New user created: {}, sending welcome notification", event.getUser().getName());
        // 发送欢迎邮件或消息...
    }
}
```

### 生命周期回调

```java
@Component
public class CacheWarmupService {

    @PostConstruct
    public void warmupCache() {
        // 容器初始化完成后预热缓存
        log.info("Warming up application cache...");
    }

    @PreDestroy
    public void cleanup() {
        // 容器关闭前释放资源
        log.info("Cleaning up resources before shutdown...");
    }
}
```

以上示例展示了 Spring IoC 容器在日常开发中最常用的能力。在本项目中，框架的事件系统（`IEvent`、`EventPusher`）、数据权限 SQL 拦截、工作流引擎等高级特性均构建于 IoC 容器之上，理解 IoC 是掌握整个框架的基础。
