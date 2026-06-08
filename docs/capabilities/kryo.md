---
name: kryo
description: 高性能 Java 序列化框架，用于数据快照
status: 已实现
scope: 后端
source: 框架:Kryo
---

## 解决什么问题

Kryo 为项目的工作流模块提供高性能序列化能力，解决了以下问题：

- **流程快照**：将完整的流程定义（FlowWork）序列化为字节数组存储，支持版本回溯
- **存储优化**：相比 Java 原生序列化，Kryo 生成的字节流更小、速度更快，降低数据库存储压力
- **版本去重**：自动判断是否存在相同版本的流程快照，避免重复存储

## 如何使用

项目使用 Kryo 5.6.2，仅在 `springboot-starter-flow` 模块的 `FlowWorkSerializable` 类中使用：

1. **序列化**：`FlowWorkSerializable.toSerializable()` 将流程对象转为 `byte[]`
2. **反序列化**：`FlowWorkSerializable.fromSerializable(byte[])` 从字节数组恢复流程对象
3. **类型注册**：需要预先注册所有参与序列化的类（包括枚举、嵌套对象等）
4. **与 FlowBackup 集成**：`FlowBackup` 持有序列化后的字节数组，通过 `resume()` 方法恢复完整流程

## 使用实例

### 流程序列化

```java
public class FlowWorkSerializable implements Serializable {

    public byte[] toSerializable() {
        Kryo kryo = new Kryo();
        // 注册所有需要序列化的类型
        kryo.register(ArrayList.class);
        kryo.register(FlowNodeSerializable.class);
        kryo.register(FlowRelationSerializable.class);
        kryo.register(FlowWorkSerializable.class);
        kryo.register(ApprovalType.class);
        kryo.register(NodeType.class);
        kryo.register(FlowButton.class);
        kryo.register(FlowButtonType.class);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeObject(output, this);
        output.close();
        return outputStream.toByteArray();
    }
}
```

### 流程反序列化

```java
public static FlowWorkSerializable fromSerializable(byte[] bytes) {
    Kryo kryo = new Kryo();
    kryo.register(ArrayList.class);
    kryo.register(FlowNodeSerializable.class);
    kryo.register(FlowRelationSerializable.class);
    kryo.register(FlowWorkSerializable.class);
    kryo.register(ApprovalType.class);
    kryo.register(NodeType.class);
    kryo.register(FlowButton.class);
    kryo.register(FlowButtonType.class);
    return kryo.readObject(new Input(bytes), FlowWorkSerializable.class);
}
```

### FlowBackup 中的使用

```java
// 创建快照：将流程定义序列化后存储
public FlowBackup(FlowWork flowWork) {
    this.bytes = flowWork.toSerializable().toSerializable();  // Kryo 序列化
    this.workVersion = flowWork.getUpdateTime();
    this.workId = flowWork.getId();
    this.createTime = System.currentTimeMillis();
}

// 恢复快照：从字节数组反序列化并重建流程对象
public FlowWork resume(FlowOperatorRepository flowOperatorRepository) {
    return FlowWorkSerializable.fromSerializable(this.bytes)
        .toFlowWork(flowOperatorRepository);
}
```
