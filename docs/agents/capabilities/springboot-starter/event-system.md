---
name: springboot-starter/event-system
module: springboot-starter
description: 事件发布-订阅系统，支持同步/异步事件、事件循环检测、事务事件
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在 DDD（领域驱动设计）架构中，领域事件是实现聚合间解耦、触发副作用的核心机制。然而在实际开发中，事件系统面临以下痛点：

1. **事件与主业务事务的边界模糊**：开发者容易将事件处理与主业务强绑定，导致事务范围膨胀、性能下降。框架需要明确"事件对主业务可成功可失败"的设计原则。
2. **同步与异步事件的区分困难**：部分场景要求事件在同一事务内同步执行（如数据校验），部分场景则需异步解耦（如发送通知）。手动管理线程池和执行模式增加复杂度。
3. **事件循环调用风险**：当 Handler A 处理 EventX 时又推送了 EventY，而 EventY 的 Handler 又推送了 EventX，就会形成无限循环。缺乏自动检测机制会导致栈溢出或系统挂起。
4. **多 Handler 执行的异常隔离**：同一事件可能被多个 Handler 订阅，某个 Handler 抛异常不应影响其他 Handler 的执行，同时需要收集所有异常统一上报。
5. **Handler 注册与排序繁琐**：手动注册处理器、维护执行顺序的代码分散且易出错。

本模块提供了一套完整的发布-订阅事件基础设施，通过 `EventPusher` 统一入口、`IEvent` 类型体系区分同步/异步、`EventTraceContext` + `EventStackContext` 实现事件链路追踪与循环检测、`ApplicationHandlerUtils` 自动匹配并按序分发 Handler，以及可选的事务提交后触发模式（`SpringTransactionEventHandler`）。

## 如何使用

### 核心接口

| 接口/类 | 说明 |
|---------|------|
| `IEvent` | 事件标记接口，继承 `Serializable`。默认视为同步事件 |
| `ISyncEvent` | 同步事件标记接口，继承 `IEvent`。事件在当前线程同步执行 |
| `IAsyncEvent` | 异步事件标记接口，继承 `IEvent`。事件在线程池中异步执行 |
| `IHandler<T extends IEvent>` | 事件处理器接口，泛型 `T` 指定订阅的事件类型 |
| `EventPusher` | 事件推送静态工具类，是发布事件的唯一入口 |
| `@Handler` | 注解，标记一个类为事件处理器 Bean（也可使用 `@Component`/`@Service`） |

### IHandler 接口方法

```java
public interface IHandler<T extends IEvent> {
    // 执行顺序，数值越小越先执行，默认为 0
    default int order() { return 0; }

    // 事件处理逻辑
    void handler(T event);

    // 异常回调，默认重新抛出异常（阻止后续 Handler 执行）
    // 可覆盖此方法实现异常吞没或自定义处理
    default void error(Exception exception) throws Exception {
        throw exception;
    }
}
```

### 事件推送

```java
// 推送事件（自动根据 IEvent 子接口判断同步/异步）
EventPusher.push(new MyEvent(data));

// 显式声明允许循环事件（跳过循环检测）
EventPusher.push(new MyEvent(data), true);
```

事件类型的判定规则：
- 实现 `IAsyncEvent` → 异步执行（线程池）
- 实现 `ISyncEvent` → 同步执行（当前线程）
- 仅实现 `IEvent` → 默认同步执行

### 配置项

```properties
# 启用事务事件模式（事件在事务提交后触发）
codingapi.framework.event.transaction.enable=true

# 异步事件线程池大小（默认值见 PropertiesContext）
codingapi.framework.handler-thread-pool-size=20
```

### 事件分发模式

框架通过 `SpringHandlerConfiguration` 自动装配事件分发器：

- **默认模式**（`SpringDefaultEventHandler`）：使用 Spring `@EventListener` 即时触发，同步事件在当前线程执行，异步事件提交到固定大小线程池。
- **事务模式**（`SpringTransactionEventHandler`）：当配置 `codingapi.framework.event.transaction.enable=true` 时激活，使用 `@TransactionalEventListener(phase = AFTER_COMMIT)` 确保事件在事务提交后才触发，避免读取到未提交的数据。设置 `fallbackExecution = true` 保证无事务上下文时也能正常执行。

两种模式互斥，事务模式优先（`@ConditionalOnMissingBean` 兜底默认模式）。

### 事件循环检测

框架通过 `EventTraceContext` 和 `EventStackContext` 实现同一条事件链路内的循环检测：

1. 每次推送事件时生成/复用 traceId，将事件类压入该 traceId 对应的事件栈。
2. 若同一 traceId 下出现相同事件类，立即抛出 `EventLoopException`，并附带完整的事件调用栈信息。
3. 事件处理完成后自动清理 traceId 和事件栈。
4. 若确实需要允许循环（如状态机重试），可调用 `EventPusher.push(event, true)` 跳过检测。

### Handler 异常处理策略

当某个 Handler 抛出异常时：
1. 若异常为 `EventLoopException`，直接向上抛出，终止后续所有 Handler。
2. 否则调用该 Handler 的 `error(exception)` 回调。
3. 若 `error()` 也抛出异常，标记为"有异常"并继续执行下一个 Handler。
4. 所有 Handler 执行完毕后，若存在异常，统一包装为 `EventException` 抛出。

## 使用实例

### 1. 定义事件

```java
package com.example.domain.order.event;

import com.codingapi.springboot.framework.event.ISyncEvent;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 订单创建事件（同步）
 */
@Getter
@AllArgsConstructor
public class OrderCreatedEvent implements ISyncEvent {
    private final String orderId;
    private final String userId;
    private final long amount;
}
```

```java
package com.example.domain.order.event;

import com.codingapi.springboot.framework.event.IAsyncEvent;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 订单通知事件（异步）
 */
@Getter
@AllArgsConstructor
public class OrderNotifyEvent implements IAsyncEvent {
    private final String orderId;
    private final String message;
}
```

### 2. 编写事件处理器

```java
package com.example.handler;

import com.codingapi.springboot.framework.event.IHandler;
import com.example.domain.order.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单创建后扣减库存（优先执行）
 */
@Slf4j
@Service
public class InventoryDeductHandler implements IHandler<OrderCreatedEvent> {

    @Override
    public int order() {
        return 1; // 最先执行
    }

    @Override
    public void handler(OrderCreatedEvent event) {
        log.info("扣减库存, orderId={}, amount={}", event.getOrderId(), event.getAmount());
        // inventoryService.deduct(event.getOrderId(), event.getAmount());
    }
}
```

```java
package com.example.handler;

import com.codingapi.springboot.framework.event.IHandler;
import com.example.domain.order.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单创建后记录积分（其次执行）
 */
@Slf4j
@Service
public class PointsRecordHandler implements IHandler<OrderCreatedEvent> {

    @Override
    public int order() {
        return 2;
    }

    @Override
    public void handler(OrderCreatedEvent event) {
        log.info("记录积分, userId={}", event.getUserId());
        // pointsService.record(event.getUserId(), event.getAmount());
    }

    @Override
    public void error(Exception exception) {
        // 积分记录失败不影响其他 Handler，仅记录日志
        log.error("积分记录失败, 但不阻断流程", exception);
    }
}
```

```java
package com.example.handler;

import com.codingapi.springboot.framework.event.IHandler;
import com.example.domain.order.event.OrderNotifyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 异步发送订单通知
 */
@Slf4j
@Service
public class OrderNotifyHandler implements IHandler<OrderNotifyEvent> {

    @Override
    public void handler(OrderNotifyEvent event) {
        log.info("发送通知, orderId={}, message={}", event.getOrderId(), event.getMessage());
        // notificationService.send(event.getOrderId(), event.getMessage());
    }
}
```

### 3. 在领域服务中推送事件

```java
package com.example.domain.order.service;

import com.codingapi.springboot.framework.event.EventPusher;
import com.example.domain.order.event.OrderCreatedEvent;
import com.example.domain.order.event.OrderNotifyEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Transactional
    public String createOrder(String userId, long amount) {
        String orderId = generateOrderId();
        // ... 持久化订单实体 ...

        // 同步事件：在当前事务内执行库存扣减、积分记录
        EventPusher.push(new OrderCreatedEvent(orderId, userId, amount));

        // 异步事件：事务外异步发送通知
        EventPusher.push(new OrderNotifyEvent(orderId, "您的订单已创建"));

        return orderId;
    }
}
```

### 4. 启用事务事件模式（可选）

若希望所有事件都在事务提交后再触发（避免 Handler 读到未提交数据），在 `application.properties` 中添加：

```properties
codingapi.framework.event.transaction.enable=true
```

此时 `SpringTransactionEventHandler` 替代默认的 `SpringDefaultEventHandler`，所有事件将在 `AFTER_COMMIT` 阶段触发。
