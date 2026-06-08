---
name: domain-proxy
description: 基于 CGLIB 的领域实体代理，自动拦截字段变更并推送 DomainChangeEvent 领域事件
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter
symbols:
  - DomainProxyFactory
  - DomainChangeInterceptor
  - DomainChangeEvent
  - DomainCreateEvent
  - DomainDeleteEvent
  - DomainPersistEvent
  - DomainEvent
  - IDomain
content_hash: dc7a9d76224be09498f0278ec05c56ea34b0c5e2dbb80850ccb826fabe721da6
---

## 解决什么问题

在 DDD 中，领域实体的变更需要通知相关组件（如记录变更日志、触发联动更新）。手动编写变更检测代码繁琐且易遗漏。本能力通过代理模式自动拦截实体字段变更，解决以下问题：

- **自动变更检测**：代理实体的 setter 方法，自动对比新旧值
- **变更事件推送**：检测到字段变更时自动推送 `DomainChangeEvent`
- **创建事件**：通过 `DomainProxyFactory.create()` 创建实体时自动推送 `DomainCreateEvent`
- **嵌套字段支持**：递归读取和对比嵌套对象的字段变更

## 如何使用

### 创建代理实体

```java
// 通过工厂创建代理实体，自动推送 DomainCreateEvent
User user = DomainProxyFactory.create(User.class, "张三", 25);
```

### 变更自动检测

```java
// 调用 setter 方法时，自动对比字段变更并推送 DomainChangeEvent
user.setName("李四");  // 自动推送 DomainChangeEvent(user, "name", "张三", "李四")
user.setAge(30);      // 自动推送 DomainChangeEvent(user, "age", 25, 30)
```

### 订阅变更事件

```java
@Service
public class ChangeLogHandler implements IHandler<DomainChangeEvent> {
    @Override
    public void handler(DomainChangeEvent event) {
        log.info("字段变更: {} {} -> {}", 
            event.getFieldName(), event.getOldValue(), event.getNewValue());
    }
}
```

## 使用实例

```java
// 创建代理实体
Order order = DomainProxyFactory.create(Order.class, orderId, amount);

// 修改字段 - 自动触发变更事件
order.setStatus("PAID");
// → DomainChangeEvent(order, "status", "PENDING", "PAID")

// 订阅处理
@Service
public class OrderChangeHandler implements IHandler<DomainChangeEvent> {
    @Override
    public void handler(DomainChangeEvent event) {
        if ("status".equals(event.getFieldName())) {
            auditLog.record(event);
        }
    }
}
```
