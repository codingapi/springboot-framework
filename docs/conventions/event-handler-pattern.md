---
name: event-handler-pattern
description: 事件处理器自动注册规范
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

框架自建的事件系统需要一套统一的 Handler 编写和注册规范，以确保：

- **自动发现**：新增 Handler 无需手动注册，Spring 容器启动时自动扫描并挂载
- **类型安全**：通过泛型 `IHandler<T extends IEvent>` 确保 Handler 只接收匹配的事件类型
- **执行顺序可控**：同一事件的多个 Handler 可通过 `order()` 控制执行顺序
- **异常隔离**：单个 Handler 的异常不会阻断其他 Handler 的执行（除非主动抛出）
- **循环检测**：自动检测事件链中的循环调用，防止无限递归

## 如何使用

### 核心接口

```java
// 事件标记接口
public interface IEvent extends Serializable {}
public interface ISyncEvent extends IEvent {}   // 同步事件
public interface IAsyncEvent extends IEvent {}   // 异步事件

// 事件处理器
public interface IHandler<T extends IEvent> {
    default int order() { return 0; }           // 执行顺序，数值越小越先执行
    void handler(T event);                       // 事件处理逻辑
    default void error(Exception exception) throws Exception {
        throw exception;                         // 默认重新抛出异常
    }
}
```

### 注册流程

1. 编写 Handler 类，实现 `IHandler<T>` 接口
2. 标记为 Spring Bean（`@Service`、`@Component` 等）
3. `HandlerBeanDefinitionRegistrar` 在启动时自动扫描所有带 `@Handler` 注解的类
4. `SpringDefaultEventHandler` / `SpringTransactionEventHandler` 构造函数中收集所有 `IHandler` Bean，注册到 `ApplicationHandlerUtils`
5. 事件推送时由 `ApplicationHandlerUtils.handler()` 按类型匹配 + order 排序后依次调用

### 推送事件

```java
// 同步推送（默认）
EventPusher.push(event);

// 指定是否允许循环事件
EventPusher.push(event, hasLoopEvent);
```

## 使用实例

### ✅ 正确示例：标准 Handler 编写

```java
@Slf4j
@Service
@AllArgsConstructor
public class LeaveHandler implements IHandler<FlowApprovalEvent> {

    private final LeaveService leaveService;

    @Override
    public void handler(FlowApprovalEvent event) {
        log.info("LeaveHandler: {}", event);
        if (event.isFinish() && event.match(LeaveForm.class)) {
            LeaveForm form = (LeaveForm) event.getBindData();
            leaveService.create(form.toLeave());
        }
    }

    @Override
    public int order() {
        return 10;  // 明确指定执行顺序
    }

    @Override
    public void error(Exception exception) throws Exception {
        log.error("LeaveHandler error", exception);
        // 自定义异常处理，不重新抛出则不会阻断后续 Handler
    }
}
```

### ✅ 正确示例：定义同步/异步事件

```java
// 同步事件 —— 在当前线程中执行
public class FlowApprovalEvent implements ISyncEvent {
    // ...
}

// 异步事件 —— 在线程池中执行
public class UserNotificationEvent implements IAsyncEvent {
    // ...
}

// 默认同步（未实现 ISyncEvent 或 IAsyncEvent 时视为同步）
public class UserEvent implements IEvent {
    // ...
}
```

### ❌ 错误示例：忘记标记为 Spring Bean

```java
// 没有 @Service/@Component，不会被自动发现和注册
public class LeaveHandler implements IHandler<FlowApprovalEvent> {
    @Override
    public void handler(FlowApprovalEvent event) {
        // 永远不会被调用
    }
}
```

### ❌ 错误示例：在 Handler 中触发相同类型的事件导致循环

```java
@Service
public class RecursiveHandler implements IHandler<UserEvent> {
    @Override
    public void handler(UserEvent event) {
        // 再次推送同类型事件 → 触发 EventLoopException
        EventPusher.push(new UserEvent());
    }
}
```

### ❌ 错误示例：在 error() 中静默吞掉异常且不记录

```java
@Override
public void error(Exception exception) throws Exception {
    // 既不记录日志也不重新抛出，问题被完全隐藏
}
```
