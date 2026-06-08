---
name: domain-event
description: 领域实体事件自动追踪机制，支持创建、变更、删除事件的自动推送与字段级变更检测
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在 DDD 实践中，领域实体的生命周期事件（创建、修改、删除）是业务流转的关键信号。手动在每个业务方法中调用 `EventPusher.push()` 存在以下痛点：

1. **侵入性强**：每个 setter 或状态变更方法都需要显式推送事件，代码冗余
2. **遗漏风险**：开发者可能忘记在某些字段变更后推送事件
3. **变更详情缺失**：普通事件只能知道"实体变了"，无法知道"哪个字段从什么值变成了什么值"
4. **实体创建事件易遗漏**：新创建的实体如果没有及时推送事件，下游监听器将无法感知

框架通过 `IDomain` 接口提供标准的持久化和删除事件入口，并通过 `DomainProxyFactory` + CGLIB 代理实现字段级变更拦截，自动推送 `DomainChangeEvent`，携带字段名、旧值、新值信息。

## 如何使用

### 1. 实体类实现 IDomain 接口

```java
public class User implements IDomain {
    private Long id;
    private String name;
    private Integer age;
    
    // getter/setter...
}
```

### 2. 使用 DomainProxyFactory 创建代理实例

通过工厂创建的实例会自动：
- 推送 `DomainCreateEvent`（创建时）
- 拦截所有带参数的 setter 方法，自动对比字段变化并推送 `DomainChangeEvent`

```java
// 创建代理实例（自动推送 DomainCreateEvent）
User user = DomainProxyFactory.create(User.class);
user.setName("张三"); // 自动推送 DomainChangeEvent(fieldName="name", oldValue=null, newValue="张三")
```

### 3. 手动触发持久化或删除事件

```java
// 保存后推送持久化事件
user.persist(); // 推送 DomainPersistEvent

// 删除前推送删除事件
user.delete(); // 推送 DomainDeleteEvent
```

### 4. 订阅领域事件

```java
@Component
public class UserChangeLogger implements IHandler<DomainChangeEvent> {
    @Override
    public void handler(DomainChangeEvent event) {
        log.info("Entity {} field '{}' changed from {} to {}",
            event.getEntityClass().getSimpleName(),
            event.getFieldName(),
            event.getOldValue(),
            event.getNewValue()
        );
    }
}

@Component
public class UserCreatedNotifier implements IHandler<DomainCreateEvent> {
    @Override
    public void handler(DomainCreateEvent event) {
        log.info("New entity created: {}", event.getEntityClass().getSimpleName());
    }
}
```

## 使用实例

### 完整示例：用户信息管理

```java
// 1. 定义用户实体
public class User implements IDomain {
    private Long id;
    private String name;
    private String email;
    private Integer status;
    
    // 必须有无参构造或有参构造供代理使用
    public User() {}
}

// 2. 监听创建事件
@Component
public class UserCreatedHandler implements IHandler<DomainCreateEvent> {
    @Override
    public int order() { return 0; }
    
    @Override
    public void handler(DomainCreateEvent event) {
        if (event.getEntity() instanceof User user) {
            log.info("User created: {}", user.getId());
            // 初始化默认配置等
        }
    }
}

// 3. 监听字段变更事件
@Component
public class UserChangeAuditHandler implements IHandler<DomainChangeEvent> {
    @Override
    public void handler(DomainChangeEvent event) {
        if (event.getEntity() instanceof User) {
            auditLogService.record(
                event.getEntityClass().getName(),
                event.getFieldName(),
                String.valueOf(event.getOldValue()),
                String.valueOf(event.getNewValue())
            );
        }
    }
}

// 4. 监听删除事件
@Component
public class UserDeletedHandler implements IHandler<DomainDeleteEvent> {
    @Override
    public void handler(DomainDeleteEvent event) {
        if (event.getEntity() instanceof User user) {
            cleanupService.cleanUserData(user.getId());
        }
    }
}

// 5. Service 中使用
@Service
public class UserService {
    
    public User createUser(String name, String email) {
        // 通过工厂创建，自动推送 DomainCreateEvent
        User user = DomainProxyFactory.create(User.class);
        user.setName(name);   // 自动推送 DomainChangeEvent
        user.setEmail(email); // 自动推送 DomainChangeEvent
        
        userRepository.save(user);
        user.persist(); // 推送 DomainPersistEvent
        return user;
    }
    
    public void updateUserName(Long userId, String newName) {
        User user = userRepository.findById(userId).orElseThrow();
        // 注意：直接从 Repository 获取的对象不会自动追踪变更
        // 如需追踪，需重新通过代理包装或使用其他 AOP 方案
        user.setName(newName);
        userRepository.save(user);
    }
    
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.delete(); // 推送 DomainDeleteEvent
        userRepository.delete(user);
    }
}
```

### 嵌套对象字段变更检测

`DomainChangeInterceptor` 支持嵌套对象的字段级变更检测：

```java
public class Address {
    private String city;
    private String street;
    // getter/setter...
}

public class User implements IDomain {
    private String name;
    private Address address;
    // getter/setter...
}

// 当 address.city 发生变化时，fieldName 为 "address.city"
// DomainChangeEvent.getFieldName() = "address.city"
// DomainChangeEvent.getOldValue() = "北京"
// DomainChangeEvent.getNewValue() = "上海"
```

### 事件类型继承关系

```
IEvent
  └── DomainEvent (com.codingapi.springboot.framework.domain.event.DomainEvent)
        ├── DomainCreateEvent   — 实体创建
        ├── DomainChangeEvent   — 字段变更（含 fieldName, oldValue, newValue）
        ├── DomainDeleteEvent   — 实体删除
        └── DomainPersistEvent  — 实体持久化
```
