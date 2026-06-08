---
name: springboot-starter/domain-proxy
module: springboot-starter
description: 领域实体变更代理，通过 CGLIB 代理拦截实体字段变更并自动推送领域事件
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在 DDD（领域驱动设计）实践中，领域实体的状态变更需要产生对应的领域事件（创建、变更、删除、持久化），以便下游处理器执行副作用操作（如发送通知、更新缓存、触发工作流等）。然而手动在每个 setter 方法中编写事件推送代码存在以下痛点：

1. **侵入性强**：每个实体类的修改方法都需要显式调用 `EventPusher.push()`，业务逻辑与事件机制耦合。
2. **容易遗漏**：开发者可能忘记在某些字段变更后推送事件，导致数据不一致。
3. **变更追踪困难**：无法自动获取字段的旧值与新值，手动记录增加出错风险。
4. **嵌套对象变更不可见**：当实体包含子对象时，子对象字段的变更更难被感知和追踪。

`domain-proxy` 能力通过 CGLIB 动态代理透明地拦截实体方法调用，自动比较字段值变化并推送 `DomainChangeEvent`，同时提供完整的实体生命周期事件（创建、持久化、删除），让领域事件的发布对业务代码零侵入。

## 如何使用

### 核心组件

| 组件 | 说明 |
|------|------|
| `IDomain` | 领域实体标记接口，提供 `persist()` 和 `delete()` 默认方法，分别推送 `DomainPersistEvent` 和 `DomainDeleteEvent` |
| `DomainProxyFactory` | 静态工厂类，通过 `create(Class<T>, Object... args)` 创建代理实例，同时自动推送 `DomainCreateEvent` |
| `DomainChangeInterceptor` | CGLIB `MethodInterceptor` 实现，拦截带参数的方法调用，对比执行前后字段值差异，自动推送 `DomainChangeEvent` |
| `DomainEvent` | 领域事件基类，携带实体引用、实体类型和时间戳 |
| `DomainCreateEvent` | 实体创建事件，由 `DomainProxyFactory.create()` 自动推送 |
| `DomainChangeEvent` | 实体字段变更事件，包含 `fieldName`、`oldValue`、`newValue` |
| `DomainDeleteEvent` | 实体删除事件，通过 `IDomain.delete()` 推送 |
| `DomainPersistEvent` | 实体持久化事件，通过 `IDomain.persist()` 推送 |

### 使用步骤

1. **定义领域实体**：创建实体类并实现 `IDomain` 接口。实体类需要有公开的构造函数供代理工厂反射调用。

2. **通过工厂创建实例**：使用 `DomainProxyFactory.create(EntityClass.class, constructorArgs...)` 代替 `new` 关键字创建实体。工厂会自动创建 CGLIB 代理并推送 `DomainCreateEvent`。

3. **正常调用业务方法**：对代理对象调用任何带参数的方法后，拦截器会自动比较所有字段（包括嵌套对象）的值变化，若有变更则推送 `DomainChangeEvent`。

4. **触发生命周期事件**：在适当时机调用 `entity.persist()` 推送持久化事件，或调用 `entity.delete()` 推送删除事件。

5. **注册事件处理器**：编写 `IHandler<DomainCreateEvent>`、`IHandler<DomainChangeEvent>` 等处理器 Bean，框架会自动扫描并注册。

### 注意事项

- 代理仅拦截**带参数**的方法调用，无参方法（如 getter）不会触发变更检测。
- 支持基本类型（String、数值、布尔、枚举等）的直接比较，以及嵌套对象的递归字段比较。
- 事件推送通过 `EventPusher` 进入框架的事件系统，遵循同步/异步分发和循环检测机制。

## 使用实例

### 定义领域实体

```java
public class User implements IDomain {

    @Getter
    private final long id;

    @Getter
    private String name;

    @Getter
    private String email;

    public User(String name, String email) {
        this.id = System.currentTimeMillis();
        this.name = name;
        this.email = email;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeEmail(String email) {
        this.email = email;
    }
}
```

### 创建代理并触发事件

```java
// 通过工厂创建代理实例，自动推送 DomainCreateEvent
User user = DomainProxyFactory.create(User.class, "张三", "zhangsan@example.com");

// 调用带参方法后，拦截器自动检测字段变更并推送 DomainChangeEvent
user.changeName("李四");      // → DomainChangeEvent(fieldName="name", oldValue="张三", newValue="李四")
user.changeEmail("li@example.com"); // → DomainChangeEvent(fieldName="email", ...)

// 手动触发持久化事件
user.persist();               // → DomainPersistEvent

// 手动触发删除事件
user.delete();                // → DomainDeleteEvent
```

### 编写事件处理器

```java
@Component
public class UserChangeHandler implements IHandler<DomainChangeEvent> {

    @Override
    public void handler(DomainChangeEvent event) {
        log.info("用户字段变更: {} = {} → {}",
            event.getFieldName(), event.getOldValue(), event.getNewValue());
    }
}

@Component
public class UserCreateHandler implements IHandler<DomainCreateEvent> {

    @Override
    public void handler(DomainCreateEvent event) {
        log.info("新用户创建: {}", event.getEntity().getClass().getSimpleName());
    }
}
```
