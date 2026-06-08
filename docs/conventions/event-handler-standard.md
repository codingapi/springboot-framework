---
name: event-handler-standard
description: 事件处理规范 — Handler 必须实现 IHandler 接口并注册为 Spring Bean，事件必须实现 IEvent 接口
status: 已实现
scope: 后端
source: 项目自有
symbols:
  - IHandler
  - IEvent
  - ISyncEvent
  - IAsyncEvent
  - EventPusher
content_hash: 0703329f337a48ff76c084efb93b0c0fcb3ef426ba4b201f81e0c28b646c7405
---

## 解决什么问题

不遵守此规范会导致：
- Handler 无法被框架自动发现和注册
- 事件类型混乱，同步/异步边界不清
- 多个 Handler 执行顺序不可控
- 事件循环引用导致系统崩溃

## 如何使用

### 事件定义规范

```java
// 事件必须实现 IEvent（或其子接口）
// 事件类应是不可变的数据载体
public class OrderCreatedEvent implements ISyncEvent {
    private final Long orderId;
    private final Long userId;
    
    public OrderCreatedEvent(Long orderId, Long userId) {
        this.orderId = orderId;
        this.userId = userId;
    }
    
    public Long getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
}
```

### 事件类型选择

| 接口 | 执行方式 | 适用场景 |
|------|----------|----------|
| `IEvent` | 同步（默认） | 一般业务事件 |
| `ISyncEvent` | 同步 | 需要保证执行顺序的事件 |
| `IAsyncEvent` | 异步（线程池） | 通知类、日志类非关键事件 |

### Handler 规范

```java
// 1. 必须注册为 Spring Bean（@Service / @Component）
// 2. 泛型参数指定订阅的事件类型
// 3. 不应在 Handler 中抛出未处理异常
@Service
public class OrderNotifyHandler implements IHandler<OrderCreatedEvent> {

    @Override
    public int order() {
        return 10;  // 排序值，数值小的先执行
    }

    @Override
    public void handler(OrderCreatedEvent event) {
        // 处理事件逻辑
        notifyService.sendOrderNotification(event.getOrderId());
    }

    @Override
    public void error(Exception exception) throws Exception {
        // 异常处理：记录日志但不影响其他 Handler
        log.error("订单通知发送失败: {}", exception.getMessage());
    }
}
```

### 推送规范

```java
// ✅ 使用 EventPusher.push() 推送
EventPusher.push(new OrderCreatedEvent(orderId, userId));

// ✅ 允许循环事件的场景（需谨慎）
EventPusher.push(event, true);

// ❌ 不要直接使用 Spring ApplicationEventPublisher
applicationEventPublisher.publishEvent(event);  // 绕过框架的事件管理
```

## 使用实例

✅ **正确示例**：
```java
// 事件定义 — 不可变数据载体
public class UserRegisteredEvent implements ISyncEvent {
    private final Long userId;
    public UserRegisteredEvent(Long userId) { this.userId = userId; }
    public Long getUserId() { return userId; }
}

// 推送
EventPusher.push(new UserRegisteredEvent(user.getId()));

// 订阅 — Spring Bean + IHandler 泛型
@Service
public class WelcomeEmailHandler implements IHandler<UserRegisteredEvent> {
    @Override
    public void handler(UserRegisteredEvent event) {
        emailService.sendWelcome(event.getUserId());
    }
}
```

❌ **错误示例**：
```java
// 事件包含可变状态
public class BadEvent implements IEvent {
    public List<String> mutableList = new ArrayList<>();  // 不应暴露可变集合
}

// Handler 不是 Spring Bean — 不会被自动注册
public class OrphanHandler implements IHandler<MyEvent> {
    @Override
    public void handler(MyEvent event) { /* 永远不会被调用 */ }
}
```
