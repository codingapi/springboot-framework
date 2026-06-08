---
name: domain-change-interceptor
description: 通过 CGLIB 代理拦截领域实体字段的 setter 方法，自动检测变更并发布 DomainChangeEvent
status: 已实现
scope: 后端
source: 项目自有
last_commit: 87c449a7
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/proxy/DomainChangeInterceptor.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/proxy/DomainProxyFactory.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/event/DomainChangeEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/event/DomainCreateEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/event/DomainDeleteEvent.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/domain/IDomain.java
---

## 解决什么问题

在 DDD（领域驱动设计）实践中，领域实体的状态变更需要被精确追踪并作为领域事件发布，以便触发审计日志、通知下游系统、维护事件溯源等。传统做法是在每个 setter 方法中手动编写变更检测和事件发布代码，导致领域模型充斥大量样板代码，且容易遗漏。

本能力通过 CGLIB 动态代理技术，在运行时拦截领域实体的所有带参数的方法调用（主要是 setter），自动对比字段变更前后的值，当检测到差异时自动发布 `DomainChangeEvent`。核心优势：

- **零侵入领域模型**：实体类无需继承特定基类或添加注解，保持纯 POJO
- **自动变更检测**：支持基本类型和嵌套对象的深层字段比较
- **完整生命周期事件**：覆盖创建（`DomainCreateEvent`）、变更（`DomainChangeEvent`）、删除（`DomainDeleteEvent`）、持久化（`DomainPersistEvent`）四种领域事件
- **与框架事件系统集成**：变更事件通过 `EventPusher` 发布，可被同步/异步 Handler 消费

## 如何使用

### 核心组件

#### IDomain — 领域对象接口

```java
public interface IDomain {
    // 发布持久化事件
    default void persist() { EventPusher.push(new DomainPersistEvent(this)); }

    // 发布删除事件
    default void delete() { EventPusher.push(new DomainDeleteEvent(this)); }
}
```

领域实体可选择性实现此接口以获得便捷的持久化和删除事件发布能力。

#### DomainProxyFactory — 代理工厂

```java
// 创建带有变更拦截的领域实体代理
public static <T> T create(Class<T> entityClass, Object... args)
```

调用 `create()` 会：
1. 通过反射调用目标类的构造函数创建实例
2. 使用 CGLIB 生成代理对象
3. 自动发布 `DomainCreateEvent`
4. 返回代理对象

#### DomainChangeInterceptor — CGLIB 方法拦截器

内部工作机制：
1. **首次拦截时**：通过 `BeanUtils.getPropertyDescriptors()` 读取实体所有属性，递归快照当前字段值到内存 Map
2. **每次 setter 调用后**：重新读取字段值并与快照对比
3. **发现差异时**：通过 `EventPusher.push()` 发布 `DomainChangeEvent`，包含字段名、旧值、新值
4. **更新快照**：将新值写入快照 Map，为下次比较做准备

#### 领域事件体系

```
DomainEvent (抽象基类，实现 IEvent)
├── entity       — 关联的实体对象
├── entityClass  — 实体类型
└── timestamp    — 事件时间戳

DomainCreateEvent extends DomainEvent   — 实体创建
DomainChangeEvent extends DomainEvent   — 字段变更（含 fieldName, oldValue, newValue）
DomainDeleteEvent extends DomainEvent   — 实体删除
DomainPersistEvent extends DomainEvent  — 实体持久化
```

### 注意事项

- 代理对象通过 CGLIB 子类化实现，要求目标类不能是 final 类，且需要有可访问的构造函数
- 变更检测基于 `equals()` 比较，自定义对象需正确实现 `equals()` 方法
- 仅对带参数的方法调用进行变更检测（即 setter），无参方法（getter）直接透传
- 支持嵌套对象的深层比较，但仅限基本类型属性的递归展开

## 使用实例

### 1. 定义领域实体

```java
public class UserEntity implements IDomain {
    private String name;
    private Integer age;
    private Address address;

    public UserEntity() {}

    public UserEntity(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    // getter/setter ...
}
```

### 2. 通过代理工厂创建实体

```java
// 创建代理对象，自动发布 DomainCreateEvent
UserEntity user = DomainProxyFactory.create(UserEntity.class, "张三", 25);

// 修改字段时自动检测变更并发布 DomainChangeEvent
user.setName("李四");  // → DomainChangeEvent(fieldName="name", oldValue="张三", newValue="李四")
user.setAge(30);      // → DomainChangeEvent(fieldName="age", oldValue=25, newValue=30)

// 持久化时发布 DomainPersistEvent
user.persist();

// 删除时发布 DomainDeleteEvent
user.delete();
```

### 3. 监听领域变更事件

```java
@Component
public class UserChangeHandler implements IHandler<DomainChangeEvent> {

    @Override
    public void handler(DomainChangeEvent event) {
        if (event.getEntity() instanceof UserEntity) {
            log.info("用户字段变更: {} = {} → {}",
                event.getFieldName(),
                event.getOldValue(),
                event.getNewValue());
        }
    }
}
```

### 4. 监听实体创建事件

```java
@Component
public class UserCreateHandler implements IHandler<DomainCreateEvent> {

    @Override
    public void handler(DomainCreateEvent event) {
        log.info("新用户创建: {}", event.getEntity().getClass().getSimpleName());
    }
}
```

### 5. 嵌套对象变更检测

```java
// 假设 UserEntity 中包含 Address 嵌套对象
user.getAddress().setCity("北京");
// → DomainChangeEvent(fieldName="address.city", oldValue="上海", newValue="北京")
```

嵌套对象的字段变更会以 `父字段.子字段` 的格式记录字段路径，便于精确定位变更位置。
