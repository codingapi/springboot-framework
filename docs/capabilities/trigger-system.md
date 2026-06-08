---
name: trigger-system
description: 触发器框架，支持定时/条件触发，提供统一的触发上下文管理
status: 已实现
scope: 后端
source: 项目自有
last_commit: 70be0d38
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/trigger/Trigger.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/trigger/TriggerHandler.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/trigger/TriggerContext.java
---

## 解决什么问题

在业务流程中，存在一类"等待特定条件满足后执行"的场景：

- 订单超时未支付自动取消
- 审批流程中等待上级审批后再触发下一步
- 数据采集达到阈值后触发告警
- 临时性的条件监听，触发一次后即失效

这类需求与事件系统（Event System）的区别在于：**Event 是消息驱动**（确定了消息内容，不确定谁消费），而 **Trigger 是订阅驱动**（先注册订阅规则，再等待匹配的消息到来触发执行）。Trigger 模式允许精确控制：

- **是否进入触发逻辑**：通过 `preTrigger()` 前置判断
- **触发后是否移除**：通过 `remove()` 控制一次性触发还是持续监听
- **按类型隔离**：不同 Trigger 类型的 Handler 互不干扰

本能力提供了一个轻量级的触发器框架，支持运行时动态注册/移除触发器，适用于需要条件触发、一次性触发或临时监听的业务场景。

## 如何使用

### 核心接口

#### Trigger — 触发器数据对象标记接口

```java
public interface Trigger {
    // 标记接口，具体触发器需定义自己的数据结构
}
```

所有触发器数据对象必须实现此接口，作为泛型约束的基础类型。

#### TriggerHandler\<T extends Trigger\> — 触发处理器

```java
public interface TriggerHandler<T extends Trigger> {
    // 前置判断：是否满足触发条件
    boolean preTrigger(T trigger);

    // 触发执行逻辑
    void trigger(T trigger);

    // 执行完成后是否移除此 Handler（默认不移除）
    default boolean remove(T trigger, boolean canTrigger) {
        return false;
    }
}
```

- `preTrigger()` 返回 `true` 时才会执行 `trigger()` 方法
- `remove()` 的 `canTrigger` 参数表示本次是否实际执行了 `trigger()` 逻辑
- `remove()` 返回 `true` 时，该 Handler 会从触发器列表中移除（一次性触发器）

#### TriggerContext — 触发器上下文（单例）

```java
// 获取单例
TriggerContext ctx = TriggerContext.getInstance();

// 注册触发处理器
void addTrigger(TriggerHandler handler)

// 触发指定类型的触发器
void trigger(Trigger trigger)

// 清空某类型的所有触发器
void clear(Class<? extends Trigger> clazz)

// 判断某类型的触发器是否为空
boolean isEmpty(Class<? extends Trigger> clazz)
```

### 工作机制

1. **注册阶段**：调用 `addTrigger(handler)` 时，框架通过反射读取 Handler 实现的 `TriggerHandler<T>` 泛型参数，自动推断其关联的 Trigger 类型，并按类型分组存储
2. **触发阶段**：调用 `trigger(triggerObj)` 时，根据 triggerObj 的实际类型查找对应的 Handler 列表，依次执行：
   - 调用 `preTrigger()` 判断是否满足条件
   - 满足条件则调用 `trigger()` 执行业务逻辑
   - 调用 `remove()` 判断是否需要移除该 Handler
3. **异常隔离**：单个 Handler 执行异常不会影响其他 Handler 的执行，异常会被捕获并记录 warn 日志

### 线程安全

- 触发器 Map 使用 `ConcurrentHashMap` 保证并发注册安全
- 每个类型的 Handler 列表使用 `CopyOnWriteArrayList` 保证遍历时的并发安全

## 使用实例

### 1. 定义触发器数据对象

```java
// 订单超时触发器
public class OrderTimeoutTrigger implements Trigger {
    private final String orderId;
    private final LocalDateTime deadline;

    public OrderTimeoutTrigger(String orderId, LocalDateTime deadline) {
        this.orderId = orderId;
        this.deadline = deadline;
    }

    public String getOrderId() { return orderId; }
    public LocalDateTime getDeadline() { return deadline; }
}
```

### 2. 实现一次性触发处理器

```java
// 订单超时自动取消（触发一次后自动移除）
public class OrderTimeoutCancelHandler implements TriggerHandler<OrderTimeoutTrigger> {

    @Override
    public boolean preTrigger(OrderTimeoutTrigger trigger) {
        // 仅在超过截止时间时才触发
        return LocalDateTime.now().isAfter(trigger.getDeadline());
    }

    @Override
    public void trigger(OrderTimeoutTrigger trigger) {
        log.info("订单 {} 超时未支付，自动取消", trigger.getOrderId());
        orderService.cancelOrder(trigger.getOrderId(), "超时未支付");
    }

    @Override
    public boolean remove(OrderTimeoutTrigger trigger, boolean canTrigger) {
        // 无论是否触发成功，都移除该 Handler（一次性）
        return true;
    }
}
```

### 3. 实现持续性触发处理器

```java
// 库存告警（持续监听，每次低于阈值都触发）
public class StockAlertTrigger implements Trigger {
    private final String productId;
    private final int currentStock;

    public StockAlertTrigger(String productId, int currentStock) {
        this.productId = productId;
        this.currentStock = currentStock;
    }

    // getter ...
}

public class StockAlertHandler implements TriggerHandler<StockAlertTrigger> {

    private static final int THRESHOLD = 10;

    @Override
    public boolean preTrigger(StockAlertTrigger trigger) {
        return trigger.getCurrentStock() < THRESHOLD;
    }

    @Override
    public void trigger(StockAlertTrigger trigger) {
        notificationService.sendAlert(
            "商品 " + trigger.getProductId() + " 库存不足，当前: " + trigger.getCurrentStock()
        );
    }

    // 使用默认 remove()，返回 false，持续监听
}
```

### 4. 注册和触发

```java
@Service
public class OrderService {

    // 创建订单时注册超时触发器
    public void createOrder(String orderId) {
        // ... 创建订单逻辑 ...

        // 注册 30 分钟超时触发器
        OrderTimeoutTrigger trigger = new OrderTimeoutTrigger(
            orderId, LocalDateTime.now().plusMinutes(30));
        TriggerContext.getInstance().addTrigger(new OrderTimeoutCancelHandler());

        // 在实际场景中，通常会结合定时任务定期调用 trigger()
    }

    // 定时检查（由 ScheduledTask 调用）
    @Scheduled(fixedRate = 60000)
    public void checkOrderTimeout() {
        OrderTimeoutTrigger trigger = new OrderTimeoutTrigger(null, null);
        // 遍历待检查订单，逐个触发
        for (String orderId : pendingOrders) {
            OrderTimeoutTrigger t = new OrderTimeoutTrigger(orderId, getOrderDeadline(orderId));
            TriggerContext.getInstance().trigger(t);
        }
    }
}
```

### 5. 清空触发器

```java
// 当订单被手动取消或支付成功时，清空该类型的所有触发器
TriggerContext.getInstance().clear(OrderTimeoutTrigger.class);

// 检查是否还有活跃的触发器
boolean hasTriggers = !TriggerContext.getInstance().isEmpty(OrderTimeoutTrigger.class);
```

### 6. Event vs Trigger 选择指南

| 场景 | 推荐 | 原因 |
|------|------|------|
| 实体状态变更后通知多个下游 | Event | 消息确定，消费者不确定 |
| 等待特定条件后执行一次性操作 | Trigger | 先订阅再等待，触发后可自移除 |
| 跨模块解耦通信 | Event | 发布-订阅天然解耦 |
| 临时性条件监听 | Trigger | 可动态注册/移除，生命周期可控 |
| 定时轮询检查条件 | Trigger | 配合 preTrigger 做条件判断 |
