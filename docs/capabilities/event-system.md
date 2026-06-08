---
name: event-system
description: 自建的领域事件发布-订阅系统，支持同步/异步事件、事务后提交、Handler自动注册排序
status: 已实现
scope: 后端
source: 项目自有
last_commit: 145aada7
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/EventPusher.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/IHandler.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/IEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/ISyncEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/IAsyncEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/DomainEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/DomainEventContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/SpringDefaultEventHandler.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/SpringTransactionEventHandler.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/HandlerBeanDefinitionRegistrar.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/event/ApplicationHandlerUtils.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/event/DomainCreateEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/event/DomainChangeEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/event/DomainDeleteEvent.java
---

## 解决什么问题

在 DDD（领域驱动设计）架构中，领域实体状态变更时需要通知其他模块执行联动逻辑（如发送通知、更新缓存、触发下游流程等）。如果直接调用会导致模块间强耦合，且主业务与分支逻辑的事务边界不清晰。

本能力提供了一套自建的发布-订阅事件系统，解决以下痛点：

- **解耦**：事件发布者无需知道有哪些订阅者，通过 `IEvent` + `IHandler` 接口完全解耦
- **同步/异步分离**：通过 `ISyncEvent` / `IAsyncEvent` 标记接口区分执行模式，异步事件由独立线程池处理
- **事务后提交**：`SpringTransactionEventHandler` 使用 `@TransactionalEventListener(AFTER_COMMIT)` 确保事件在主事务提交后才触发，避免脏数据
- **Handler 自动注册与排序**：所有 `IHandler` 实现类通过 Spring Bean 自动扫描注册，支持 `order()` 控制执行顺序
- **循环检测**：内置 `EventTraceContext` 追踪事件链，防止事件循环触发
- **异常隔离**：多 Handler 场景下，单个 Handler 异常通过 `error()` 回调处理，不影响其他 Handler 执行

## 如何使用

### 核心接口

| 接口/类 | 说明 |
|---------|------|
| `IEvent` | 事件标记接口，继承 `Serializable` |
| `ISyncEvent extends IEvent` | 同步事件标记 |
| `IAsyncEvent extends IEvent` | 异步事件标记 |
| `IHandler<T extends IEvent>` | 事件处理器，泛型绑定事件类型 |
| `EventPusher` | 静态工具类，事件推送入口 |
| `DomainEvent` (domain包) | 领域实体事件基类，携带 entity、entityClass、timestamp |
| `DomainCreateEvent` | 实体创建事件 |
| `DomainChangeEvent` | 实体字段变更事件（含 fieldName、oldValue、newValue） |
| `DomainDeleteEvent` | 实体删除事件 |

### 注入方式

无需手动注入。`HandlerBeanDefinitionRegistrar` 在 Spring 启动时自动扫描所有标注 `@Handler` 的 Bean 并注册到 `ApplicationHandlerUtils`。

### 配置项

```properties
# 异步事件线程池大小（默认20）
codingapi.framework.handler-thread-pool-size=20
```

### 两种事件分发器

- **`SpringDefaultEventHandler`**：使用 `@EventListener`，事件立即触发
- **`SpringTransactionEventHandler`**：使用 `@TransactionalEventListener(AFTER_COMMIT, fallbackExecution=true)`，事务提交后触发

## 使用实例

### 1. 定义自定义事件

```java
// 同步事件
public class OrderCreatedEvent implements ISyncEvent {
    private final String orderId;
    
    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderId() { return orderId; }
}
```

### 2. 编写 Handler

```java
@Handler
@Component
public class OrderNotificationHandler implements IHandler<OrderCreatedEvent> {

    @Override
    public int order() {
        return 10; // 排序值，越小越先执行
    }

    @Override
    public void handler(OrderCreatedEvent event) {
        // 发送订单创建通知
        System.out.println("Order created: " + event.getOrderId());
    }

    @Override
    public void error(Exception exception) throws Exception {
        // 异常回调，记录日志但不阻止后续 Handler
        log.error("Notification failed", exception);
    }
}
```

### 3. 推送事件

```java
// 推送同步事件
EventPusher.push(new OrderCreatedEvent("ORD-001"));

// 推送异步事件（需事件类实现 IAsyncEvent）
EventPusher.push(new AsyncLogEvent("log message"));

// 允许循环事件（跳过循环检测）
EventPusher.push(event, true);
```

### 4. 使用领域实体事件

```java
// 实体变更事件携带字段级别信息
DomainChangeEvent changeEvent = new DomainChangeEvent(
    userEntity,       // 实体对象
    "status",         // 字段名
    "ACTIVE",         // 旧值
    "INACTIVE"        // 新值
);
EventPusher.push(changeEvent);
```
