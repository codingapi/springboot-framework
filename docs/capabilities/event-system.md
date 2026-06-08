---
name: event-system
description: 发布-订阅事件系统，支持同步/异步事件、Handler排序、循环检测与事务集成
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter
symbols:
  - EventPusher
  - IEvent
  - ISyncEvent
  - IAsyncEvent
  - IHandler
  - ApplicationHandlerUtils
  - DomainEventContext
  - EventStackContext
  - EventTraceContext
  - DomainEvent
  - SpringEventHandler
  - SpringDefaultEventHandler
  - SpringTransactionEventHandler
  - HandlerBeanDefinitionRegistrar
content_hash: 0703329f337a48ff76c084efb93b0c0fcb3ef426ba4b201f81e0c28b646c7405
---

## 解决什么问题

在 DDD 架构中，领域事件是解耦聚合根之间通信的核心机制。本事件系统解决了以下问题：

- **领域事件解耦**：聚合根通过事件通信，无需直接依赖
- **同步/异步分离**：通过接口标记区分事件类型，由框架自动调度
- **循环事件检测**：自动检测事件嵌套推送中的循环引用，防止无限递归
- **Handler 排序**：多个 Handler 订阅同一事件时，可通过 `order()` 控制执行顺序
- **异常隔离**：每个 Handler 的异常通过独立回调处理，不影响其他 Handler

## 如何使用

### 核心接口

- `IEvent` — 事件标记接口（extends Serializable）
- `ISyncEvent extends IEvent` — 同步事件标记
- `IAsyncEvent extends IEvent` — 异步事件标记
- `IHandler<T extends IEvent>` — 事件处理器，泛型绑定事件类型

### 推送事件

```java
// 推送事件（默认同步，自动检测循环）
EventPusher.push(new MyEvent());

// 允许循环事件
EventPusher.push(new MyEvent(), true);
```

### 订阅事件

```java
@Service
public class MyHandler implements IHandler<MyEvent> {

    @Override
    public int order() {
        return 0; // 排序，默认0
    }

    @Override
    public void handler(MyEvent event) {
        // 处理事件
    }

    @Override
    public void error(Exception exception) throws Exception {
        // 异常回调
        throw exception;
    }
}
```

### 注册方式

Handler 只需声明为 Spring Bean（`@Service` / `@Component`），框架通过 `HandlerBeanDefinitionRegistrar` 自动扫描所有 `IHandler` 实现并注册到 `ApplicationHandlerUtils`。

## 使用实例

```java
// 定义事件
public class UserCreatedEvent implements ISyncEvent {
    private final Long userId;
    public UserCreatedEvent(Long userId) { this.userId = userId; }
    public Long getUserId() { return userId; }
}

// 推送
EventPusher.push(new UserCreatedEvent(user.getId()));

// 订阅
@Service
public class NotifyHandler implements IHandler<UserCreatedEvent> {
    @Override
    public void handler(UserCreatedEvent event) {
        // 发送通知
    }
}
```
