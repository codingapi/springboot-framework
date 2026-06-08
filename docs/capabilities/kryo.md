---
name: kryo
description: Kryo 高性能序列化库 — 用于工作流数据快照的深拷贝与持久化
status: 已实现
scope: 后端
source: 框架:Kryo
import: com.esotericsoftware:kryo
framework_version: 5.6.2
---

## 解决什么问题

工作流引擎在审批过程中需要保存业务数据的快照（`BindDataSnapshot`），以便后续审批人能看到提交时的数据状态。Kryo 提供了高效的二进制序列化方案：

- **深拷贝**：将绑定数据深拷贝为快照，避免引用污染
- **高性能**：比 Java 原生序列化快 10 倍以上
- **紧凑**：序列化后的二进制数据体积小，适合数据库存储
- **类型注册**：通过预注册类型优化序列化效率

## 如何使用

### 基本序列化

```java
Kryo kryo = new Kryo();
kryo.setRegistrationRequired(false);

// 序列化
Output output = new Output(4096, -1);
kryo.writeObject(output, myObject);
byte[] bytes = output.toBytes();

// 反序列化
Input input = new Input(bytes);
MyObject restored = kryo.readObject(input, MyObject.class);
```

### 深拷贝

```java
Kryo kryo = new Kryo();
MyObject copy = kryo.copy(original);
```

### 本框架中的使用

工作流引擎的 `BindDataSnapshot` 使用 Kryo 保存审批时的业务数据快照：

```java
// 提交审批时自动保存快照
FlowResult result = flowService.submit(processId, opinion, operator);
// 内部：kryo.copy(bindData) → 快照存储
```

## 使用实例

```java
// 工作流快照恢复
FlowDetail detail = flowService.detail(processId);
IBindData snapshot = detail.getSnapshot();
// snapshot 保存的是提交审批时的数据状态，而非当前最新数据
```
