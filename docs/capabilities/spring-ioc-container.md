---
name: spring-ioc-container
description: Spring IoC 容器提供依赖注入、Bean 生命周期管理、AOP 支持
status: 已实现
scope: 后端
source: 框架:Spring Framework
---

## 解决什么问题

Spring IoC（控制反转）容器是项目的核心基础设施，解决了以下问题：

- **对象创建与组装**：自动管理 Bean 的创建、依赖注入和生命周期，避免手动 new 对象和硬编码依赖
- **模块解耦**：通过接口注入而非具体类依赖，使各 starter 模块可以独立开发和测试
- **横切关注点**：通过 AOP 支持事务管理、日志记录等横切逻辑的统一处理
- **自动配置**：Spring Boot 的条件化自动配置机制使框架的各 starter 模块可按需激活

## 如何使用

项目基于 Spring Boot 3.3.5（Java 17），使用方式遵循标准 Spring Boot 约定：

1. **依赖注入**：使用构造器注入（推荐 `@AllArgsConstructor` + `final` 字段）或 `@Autowired`
2. **Bean 声明**：使用 `@Component`、`@Service`、`@Repository`、`@Configuration` 等注解
3. **自动配置注册**：各 starter 模块通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 和 `META-INF/spring.factories` 双重注册
4. **ImportBeanDefinitionRegistrar**：框架使用此扩展点动态注册 Bean，如 `HandlerBeanDefinitionRegistrar` 自动扫描事件处理器

## 使用实例

### 构造器注入（项目推荐方式）

```java
@Slf4j
@Service
@AllArgsConstructor
public class LeaveHandler implements IHandler<FlowApprovalEvent> {

    private final LeaveService leaveService;  // 自动注入

    @Override
    public void handler(FlowApprovalEvent event) {
        if (event.isFinish() && event.match(LeaveForm.class)) {
            LeaveForm form = (LeaveForm) event.getBindData();
            leaveService.create(form.toLeave());
        }
    }
}
```

### Repository 实现注入

```java
@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userEntityRepository;  // JPA Repository 自动注入

    @Override
    public User getUserByUsername(String username) {
        return UserConvertor.convert(
            userEntityRepository.getUserEntityByUsername(username),
            userEntityRepository
        );
    }
}
```

### 自定义 Bean 注册（ImportBeanDefinitionRegistrar）

```java
@Configuration
public class HandlerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        RegisterBeanScanner scanner = new RegisterBeanScanner(importingClassMetadata, Handler.class);
        List<BeanDefinition> beanDefinitions = scanner.findBeanDefinitions();
        for (BeanDefinition candidate : beanDefinitions) {
            registry.registerBeanDefinition(candidate.getBeanClassName(), candidate);
        }
    }
}
```
