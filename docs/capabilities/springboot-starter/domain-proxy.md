---
name: springboot-starter/domain-proxy
module: springboot-starter
description: 基于 CGLIB 的实体代理工厂，在创建时发布 DomainCreateEvent，在 setter 触发时对比字段差异并发布 DomainChangeEvent
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter"
symbols:
  - DomainProxyFactory
  - DomainChangeInterceptor
content_hash: 454f65c620ab3f109fa77bc45e84b5926cb90a12a0c73f0dbffc52124879d538
---

## 解决什么问题

在 DDD 实践中，实体字段变更需要自动触发领域事件（`DomainChangeEvent`）以便：

- 审计日志（谁在何时改了什么字段）
- 联动业务（金额变更触发风控、状态变更触发通知）
- 事件溯源（Event Sourcing）

手动在每个 setter 中编写事件发布代码会污染业务代码。本能力通过 CGLIB 动态代理在 setter 调用前后自动对比字段差异，无侵入地发布变更事件。

## 如何使用

**1. 工厂创建实体（推荐）**

```java
// 触发顺序：构造 → 创建 DomainCreateEvent → setter 触发 → 对比字段 → 发布 DomainChangeEvent
User user = DomainProxyFactory.create(User.class, "alice", 25);
user.setName("bob");  // 自动发布 DomainChangeEvent("name", "alice", "bob")
```

**2. 事件订阅**

```java
@Component
public class UserChangeLogHandler implements IHandler<DomainChangeEvent> {
    @Override
    public Class<DomainChangeEvent> getEventClass() { return DomainChangeEvent.class; }

    @Override
    public void handler(DomainChangeEvent event) {
        // event.getEntity() / getFieldName() / getOldValue() / getNewValue()
        log.info("entity={} field={} {} -> {}",
            event.getEntity().getClass().getSimpleName(),
            event.getFieldName(),
            event.getOldValue(), event.getNewValue());
    }
}
```

**3. 支持嵌套对象字段**

`DomainChangeInterceptor.readFields` 递归读取对象属性，对嵌套对象的字段变更会以 `parent.child` 形式发布（参见 `compareAndUpdateField` 方法）。

**4. 适用范围**

- 仅当方法有参数时（>0）才走对比逻辑
- 支持 8 种基本类型 + String + Enum + Class 的值比较
- 不支持集合类型（List/Map）的内部元素对比

## 使用实例

```java
// 1. 基本用法
Order order = DomainProxyFactory.create(Order.class, 1001L, "PENDING");
order.setStatus("PAID");
// 自动发布 DomainChangeEvent(target=order, fieldName="status", oldValue="PENDING", newValue="PAID")

// 2. 嵌套对象字段变更
User user = DomainProxyFactory.create(User.class, "alice", new Profile("Beijing"));
user.getProfile().setCity("Shanghai");
// 发布 DomainChangeEvent(fieldName="profile.city", oldValue="Beijing", newValue="Shanghai")

// 3. 配合 EventPusher 与事务事件
// 配合 codingapi.framework.event.transaction.enable=true 可在事务提交后落库审计日志
```
