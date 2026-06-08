---
name: event-system
description: 基于发布-订阅模式的事件系统，支持同步/异步事件处理、事件排序和异常隔离
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在领域驱动设计（DDD）中，领域事件是实现业务解耦的核心机制。传统 Spring Event 存在以下问题：

1. **事务耦合**：Spring Event 默认与主业务事务绑定，事件失败会影响主流程
2. **缺乏类型安全**：事件处理器需要手动判断事件类型
3. **缺少执行顺序控制**：多个处理器无法保证执行顺序
4. **异常传播**：一个处理器异常会中断其他处理器的执行
5. **循环调用检测缺失**：事件之间可能形成循环依赖导致栈溢出

框架自建的事件系统通过 `IEvent` + `IHandler<T>` 的泛型绑定、`order()` 排序机制、`error()` 异常回调以及 `EventLoopException` 循环检测，解决了上述所有问题。同时支持同步（`ISyncEvent`）和异步（`IAsyncEvent`）两种执行模式，异步事件可配置线程池大小。

## 如何使用

### 1. 定义事件

实现 `IEvent` 接口或其子接口：

```java
// 同步事件 - 在当前线程立即执行
public class UserCreatedEvent implements ISyncEvent {
    private final Long userId;
    
    public UserCreatedEvent(Long userId) {
        this.userId = userId;
    }
}

// 异步事件 - 在线程池中异步执行
public class NotificationEvent implements IAsyncEvent {
    private final String message;
    
    public NotificationEvent(String message) {
        this.message = message;
    }
}
```

### 2. 实现事件处理器

实现 `IHandler<T extends IEvent>` 接口，泛型参数指定要订阅的事件类型：

```java
@Component
public class UserCreatedHandler implements IHandler<UserCreatedEvent> {
    
    // 可选：指定执行顺序，数值越小优先级越高
    @Override
    public int order() {
        return 0;
    }
    
    @Override
    public void handler(UserCreatedEvent event) {
        // 处理用户创建逻辑
        System.out.println("User created: " + event.getUserId());
    }
    
    // 可选：自定义异常处理，避免中断其他处理器
    @Override
    public void error(Exception exception) throws Exception {
        log.error("Failed to handle user created event", exception);
        // 不抛出异常，允许后续处理器继续执行
    }
}
```

### 3. 推送事件

使用静态方法 `EventPusher.push()` 推送事件：

```java
// 推送同步事件
EventPusher.push(new UserCreatedEvent(1L));

// 推送异步事件
EventPusher.push(new NotificationEvent("Welcome!"));

// 禁用循环检测（仅在明确知道无循环风险时使用）
EventPusher.push(event, true);
```

### 4. 配置异步线程池

在 `application.properties` 中配置：

```properties
codingapi.framework.handler-thread-pool-size=20
```

### 5. 启用事务后提交触发

如需在事务提交后再触发事件处理器，启用事务事件监听器：

```properties
codingapi.framework.event.transaction.enable=true
```

启用后将使用 `@TransactionalEventListener(phase = AFTER_COMMIT)` 替代默认的 `@EventListener`。

## 使用实例

### 完整示例：订单创建流程

```java
// 1. 定义订单创建事件
public class OrderCreatedEvent implements ISyncEvent {
    private final String orderId;
    private final BigDecimal amount;
    
    public OrderCreatedEvent(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}

// 2. 库存扣减处理器（优先执行）
@Component
public class InventoryDeductHandler implements IHandler<OrderCreatedEvent> {
    @Override
    public int order() {
        return 0; // 最高优先级
    }
    
    @Override
    public void handler(OrderCreatedEvent event) {
        inventoryService.deduct(event.getOrderId(), event.getAmount());
    }
}

// 3. 积分累加处理器（次优先）
@Component
public class PointsAccumulateHandler implements IHandler<OrderCreatedEvent> {
    @Override
    public int order() {
        return 10;
    }
    
    @Override
    public void handler(OrderCreatedEvent event) {
        pointsService.addPoints(event.getOrderId());
    }
    
    @Override
    public void error(Exception exception) throws Exception {
        // 积分失败不影响主流程，仅记录日志
        log.warn("Points accumulation failed for order: {}", 
                 event.getOrderId(), exception);
    }
}

// 4. 发送通知处理器（最后执行）
@Component
public class NotificationSendHandler implements IHandler<OrderCreatedEvent> {
    @Override
    public int order() {
        return 20;
    }
    
    @Override
    public void handler(OrderCreatedEvent event) {
        notificationService.sendOrderConfirmation(event.getOrderId());
    }
}

// 5. 在 Service 中推送事件
@Service
public class OrderService {
    public void createOrder(CreateOrderCommand cmd) {
        Order order = new Order(cmd);
        orderRepository.save(order);
        
        // 推送事件，三个 Handler 按 order 顺序依次执行
        EventPusher.push(new OrderCreatedEvent(order.getId(), order.getAmount()));
    }
}
```

### 循环调用防护

当检测到事件循环时，框架会自动抛出 `EventLoopException`：

```java
// 错误示例：A 触发 B，B 又触发 A
public class EventA implements ISyncEvent {}
public class EventB implements ISyncEvent {}

@Component
public class HandlerA implements IHandler<EventA> {
    @Override
    public void handler(EventA event) {
        EventPusher.push(new EventB()); // 触发 B
    }
}

@Component
public class HandlerB implements IHandler<EventB> {
    @Override
    public void handler(EventB event) {
        EventPusher.push(new EventA()); // 再次触发 A → 抛出 EventLoopException
    }
}
```
