---
name: springboot-starter/event-driven-convention
module: springboot-starter
description: 事件驱动开发规范，定义 IEvent/IHandler 的使用约定和事件处理模式
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter"
content_hash: 02975087ebd599ed5978c834297c16c37ae4e31ec37d964d753353b1de9fb07d
---

## 解决什么问题

不遵守事件驱动开发规范会导致以下问题：

1. **事务耦合风险**：将事件处理与主业务事务强绑定，导致分支业务的失败回滚整个主事务，破坏领域事件的独立性原则。当分支逻辑（如通知、日志、缓存更新）出错时，不应影响核心业务流程的提交。

2. **事件类型混乱**：不区分同步事件与异步事件，导致关键业务事件被异步执行产生时序问题，或非关键事件被同步阻塞影响主流程性能。

3. **Handler 注册失效**：未通过 `@Service` 注解将 Handler 注册为 Spring Bean，导致框架无法自动发现处理器，事件推送后无响应且难以排查。

4. **执行顺序不可控**：多个 Handler 订阅同一事件时未指定 `order()`，导致处理顺序依赖 Bean 加载顺序，在不同环境或重启后行为不一致。

5. **异常处理缺失**：未实现 `error()` 回调或直接在 `handler()` 中抛出未捕获异常，导致后续 Handler 被跳过且错误信息丢失，事件链中断而无法感知。

6. **绕过 EventPusher 直接调用**：手动实例化 Handler 或直接调用处理方法，绕过框架的循环检测、线程池调度和上下文传递机制，引发循环事件死锁或丢失链路追踪信息。

## 如何使用

### 规则 1：事件类必须实现 IEvent 接口

所有事件类必须实现 `IEvent` 接口。根据执行方式选择子接口：

- **同步事件**：实现 `ISyncEvent`，在当前线程内按顺序执行所有 Handler
- **异步事件**：实现 `IAsyncEvent`，由框架线程池异步调度执行

事件类应为纯数据载体，不包含业务逻辑，并实现 `Serializable` 以支持序列化传输。

### 规则 2：事件处理器必须实现 IHandler<T extends IEvent> 接口

Handler 通过泛型参数声明订阅的事件类型。框架在启动时通过 `HandlerBeanDefinitionRegistrar` 自动扫描所有 `IHandler` 实现并注册到 `ApplicationHandlerUtils`。

```java
public interface IHandler<T extends IEvent> {
    default int order() { return 0; }
    void handler(T event);
    default void error(Exception exception) throws Exception { throw exception; }
}
```

### 规则 3：事件处理器通过 @Service 注解注册为 Spring Bean

Handler 必须标注 `@Service`（或 `@Component`）注解，确保被 Spring 容器管理。未注册为 Bean 的 Handler 不会被框架发现和调用。

### 规则 4：事件推送统一使用 EventPusher.push() 静态方法

所有事件推送必须通过 `EventPusher.push(event)` 发起，禁止手动调用 Handler。框架内置循环事件检测机制，当检测到事件循环推送时自动抛出异常。若确认需要允许循环事件，可使用 `EventPusher.push(event, true)` 关闭检测。

### 规则 5：事件不应与主业务强耦合事务绑定

事件处理的核心理念是**解耦**。事件对于主业务来说可成功可失败，成功与失败都不应强关联主体业务。若分支逻辑必须与主事务保持一致，应直接使用服务调用而非事件机制。

### 规则 6：多个 Handler 通过 order() 方法控制执行顺序

当多个 Handler 订阅同一事件时，通过重写 `order()` 方法指定执行优先级。数值越小越先执行，默认值为 0。相同 order 值的 Handler 执行顺序不确定。

### 规则 7：Handler 的 error() 回调处理异常

`error()` 方法接收 Handler 执行过程中抛出的异常。默认实现会重新抛出异常，这将**阻止后续 Handler 的执行**。若希望某个 Handler 的失败不影响其他 Handler，应在 `error()` 中记录日志而不重新抛出。

## 使用实例

### ✅ 正确示例

```java
// 1. 定义同步事件
public class UserCreatedEvent implements ISyncEvent {
    private final String userId;
    private final String username;

    public UserCreatedEvent(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
}

// 2. 定义异步事件
public class UserNotificationEvent implements IAsyncEvent {
    private final String userId;
    private final String message;

    public UserNotificationEvent(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() { return userId; }
    public String getMessage() { return message; }
}

// 3. 注册 Handler 并指定执行顺序
@Service
public class UserCacheRefreshHandler implements IHandler<UserCreatedEvent> {

    @Override
    public int order() {
        return 1; // 优先刷新缓存
    }

    @Override
    public void handler(UserCreatedEvent event) {
        cacheService.refreshUserCache(event.getUserId());
    }

    @Override
    public void error(Exception exception) {
        // 缓存刷新失败不影响后续 Handler，仅记录日志
        log.warn("缓存刷新失败: {}", exception.getMessage());
    }
}

@Service
public class UserAuditLogHandler implements IHandler<UserCreatedEvent> {

    @Override
    public int order() {
        return 2; // 缓存之后写审计日志
    }

    @Override
    public void handler(UserCreatedEvent event) {
        auditService.logUserCreation(event.getUserId(), event.getUsername());
    }
}

// 4. 在业务中推送事件
@Service
public class UserService {

    public User createUser(CreateUserCommand command) {
        User user = userRepository.save(command.toEntity());
        // 通过 EventPusher 推送，不直接调用 Handler
        EventPusher.push(new UserCreatedEvent(user.getId(), user.getUsername()));
        return user;
    }
}
```

### ❌ 错误示例

```java
// 错误 1：事件未实现 IEvent 接口
public class UserCreatedEvent {  // ❌ 缺少 ISyncEvent / IAsyncEvent
    private String userId;
}

// 错误 2：Handler 未注册为 Spring Bean
public class UserCacheHandler implements IHandler<UserCreatedEvent> {  // ❌ 缺少 @Service
    @Override
    public void handler(UserCreatedEvent event) {
        cacheService.refreshUserCache(event.getUserId());
    }
}

// 错误 3：在事务中强绑定事件结果
@Transactional
public User createUser(CreateUserCommand command) {
    User user = userRepository.save(command.toEntity());
    try {
        EventPusher.push(new UserCreatedEvent(user.getId(), user.getUsername()));
    } catch (Exception e) {
        throw new RuntimeException("事件处理失败，回滚事务", e);  // ❌ 事件失败不应回滚主事务
    }
    return user;
}

// 错误 4：手动调用 Handler 绕过框架
@Service
public class UserService {
    @Autowired
    private UserCacheRefreshHandler cacheHandler;

    public User createUser(CreateUserCommand command) {
        User user = userRepository.save(command.toEntity());
        cacheHandler.handler(new UserCreatedEvent(user.getId(), user.getUsername()));  // ❌ 绕过 EventPusher
        return user;
    }
}

// 错误 5：未处理异常导致事件链中断
@Service
public class FragileHandler implements IHandler<UserCreatedEvent> {
    @Override
    public void handler(UserCreatedEvent event) {
        externalService.notify(event.getUserId());  // 可能抛出异常
    }
    // ❌ 未重写 error()，默认重新抛出异常，阻止后续 Handler 执行
}

// 错误 6：多个 Handler 未指定 order，执行顺序不可预测
@Service
public class HandlerA implements IHandler<UserCreatedEvent> {
    @Override
    public void handler(UserCreatedEvent event) { /* ... */ }
    // ❌ 未重写 order()，默认 0，与 HandlerB 顺序不确定
}

@Service
public class HandlerB implements IHandler<UserCreatedEvent> {
    @Override
    public void handler(UserCreatedEvent event) { /* ... */ }
    // ❌ 未重写 order()，默认 0，与 HandlerA 顺序不确定
}
```
