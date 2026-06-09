---
name: springboot-starter/domain-event
module: springboot-starter
description: 领域事件总线（同步/异步/事务后），提供事件栈追踪、循环事件检测与全局 Handler 调度
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter"
symbols:
  - DomainEvent
  - EventPusher
  - DomainEventContext
  - EventStackContext
  - EventTraceContext
  - SpringEventHandler
  - SpringDefaultEventHandler
  - SpringTransactionEventHandler
  - HandlerBeanDefinitionRegistrar
  - SpringEventConfiguration
  - SpringHandlerConfiguration
content_hash: c6e83b55164a8e318f7ea08470136960bc1b4a97ef0f3e87cbe913b14128869a
---

## 解决什么问题

在 DDD 场景下，领域实体产生的业务事件（如创建、修改、删除）需要在不同模块/层级间可靠传播，且需要：

- 同步/异步两种触发模式
- 事务提交后再触发（避免回滚后副作用）
- 自动追踪事件链（traceId），并检测事件循环
- 集中收集所有 `IHandler` 进行统一调度
- 可在 `FrameworkProperties` 中配置线程池大小

## 如何使用

**1. 业务事件定义**

```java
// 同步事件
public class OrderPaidEvent implements ISyncEvent {
    private final Order order;
    public OrderPaidEvent(Order order) { this.order = order; }
    public Order getOrder() { return order; }
}

// 异步事件
public class OrderShippedEvent implements IAsyncEvent { ... }
```

**2. 发布事件**

```java
EventPusher.push(new OrderPaidEvent(order));
// 框架根据事件是否实现 ISyncEvent/IAsyncEvent 自动选择同步或异步
// 也可直接传 IEvent，由 push 内部按接口类型分发
```

**3. 订阅事件（实现 IHandler）**

```java
@Component
public class OrderPaidHandler implements IHandler<OrderPaidEvent> {
    @Override
    public Class<OrderPaidEvent> getEventClass() { return OrderPaidEvent.class; }

    @Override
    public void handler(OrderPaidEvent event) {
        // 处理逻辑
    }
}
```

**4. 启用事务后事件（可选）**

```yaml
codingapi:
  framework:
    event:
      transaction:
        enable: true   # 启用 SpringTransactionEventHandler（在 @Transactional commit 后触发）
```

**5. 线程池配置**

```yaml
codingapi:
  framework:
    handler-thread-pool-size: 10
```

## 使用实例

```java
// 1. 实体创建时自动触发 DomainCreateEvent（与 DomainProxyFactory 配合使用）
T entity = DomainProxyFactory.create(MyEntity.class, "arg");

// 2. 业务模块内手动发布
EventPusher.push(new OrderCancelledEvent(orderId));

// 3. 事务提交后再异步处理
@Transactional
public void completeOrder(Long orderId) {
    orderRepository.update(orderId, OrderStatus.PAID);
    EventPusher.push(new OrderCompletedEvent(orderId));
    // 因配置 codingapi.framework.event.transaction.enable=true，
    // handler 将在 commit 后触发
}

// 4. 错误处理：循环事件自动抛 EventLoopException
// EventStackContext 检测到同一事件类在同一 traceId 中重复出现时
// 会清理 trace 状态并抛出异常，避免无限递归
```
