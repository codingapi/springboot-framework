---
name: esotericsoftware/kryo
module: esotericsoftware
description: Kryo 高性能序列化框架，提供快速的 Java 对象序列化
status: 已实现
scope: 后端
source: 框架:esotericsoftware
import: "com.esotericsoftware:kryo"
framework_version: 5.6.2
---

## 解决什么问题

Kryo 是一个快速、高效的 Java 对象序列化框架，主要解决以下问题：

1. **Java 原生序列化性能低下**：Java 内置的 `Serializable` 机制序列化后的字节流体积大、速度慢，不适合高性能场景。
2. **存储与传输效率**：在需要将复杂对象持久化到数据库或通过网络传输时，Kryo 能显著减少数据体积并提升处理速度。
3. **工作流快照存储**：在本框架的工作流引擎（`springboot-starter-flow`）中，流程定义数据需要以紧凑的二进制格式存储为版本快照，Kryo 提供了比 JSON/XML 更高效的序列化方案。

适用场景包括：缓存序列化、RPC 通信、对象深拷贝、数据库 BLOB 字段存储、分布式会话共享等对序列化性能敏感的场景。

## 如何使用

### Maven 依赖

```xml
<dependency>
    <groupId>com.esotericsoftware</groupId>
    <artifactId>kryo</artifactId>
    <version>5.6.2</version>
</dependency>
```

### 核心 API

| 类/接口 | 说明 |
|---------|------|
| `Kryo` | 序列化引擎核心类，负责注册类和执行序列化/反序列化操作 |
| `Output` | 输出流包装器，将序列化数据写入底层 OutputStream |
| `Input` | 输入流包装器，从底层 InputStream 读取序列化数据 |
| `Serializer<T>` | 自定义序列化器接口，用于控制特定类型的序列化行为 |

### 基本用法

1. 创建 `Kryo` 实例
2. 注册需要序列化的类（可选但推荐，可减小输出体积）
3. 使用 `writeObject()` / `readObject()` 进行序列化/反序列化

> ⚠️ **注意**：`Kryo` 实例不是线程安全的，在多线程环境中应使用 `ThreadLocal` 或对象池进行管理。

## 使用实例

### 基础序列化与反序列化

```java
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;

// 定义实体类
public class User implements Serializable {
    private String name;
    private int age;

    // getter/setter 省略
}

// 序列化
Kryo kryo = new Kryo();
kryo.register(User.class);

ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
Output output = new Output(outputStream);

User user = new User("张三", 28);
kryo.writeObject(output, user);
output.close();

byte[] bytes = outputStream.toByteArray();

// 反序列化
Input input = new Input(bytes);
User restored = kryo.readObject(input, User.class);
input.close();

System.out.println(restored.getName()); // 张三
```

### 框架中的实际应用：工作流快照序列化

以下是 `springboot-starter-flow` 模块中使用 Kryo 对流程定义进行序列化的实际代码：

```java
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class FlowWorkSerializable implements Serializable {

    private long id;
    private String code;
    private String title;
    private List<FlowNodeSerializable> nodes;
    private List<FlowRelationSerializable> relations;
    // ... 其他字段

    /**
     * 将流程定义序列化为字节数组，用于持久化存储
     */
    public byte[] toSerializable() {
        Kryo kryo = new Kryo();
        // 注册所有涉及的类型，减小序列化体积
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

    /**
     * 从字节数组反序列化恢复流程定义
     */
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
}
```

### 线程安全的使用方式

```java
// 使用 ThreadLocal 保证线程安全
private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
    Kryo kryo = new Kryo();
    kryo.register(User.class);
    kryo.register(ArrayList.class);
    kryo.setReferences(true);       // 支持循环引用
    kryo.setRegistrationRequired(false); // 允许未注册的类（开发阶段方便调试）
    return kryo;
});

// 使用时获取线程本地实例
Kryo kryo = kryoThreadLocal.get();
Output output = new Output(new ByteArrayOutputStream());
kryo.writeObject(output, user);
```
