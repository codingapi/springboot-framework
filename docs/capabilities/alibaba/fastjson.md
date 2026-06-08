---
name: alibaba/fastjson
module: alibaba
description: Fastjson JSON 序列化库，提供高性能 JSON 解析和序列化
status: 已实现
scope: 后端
source: 框架:alibaba
import: "com.alibaba:fastjson"
framework_version: 2.0.53
---

## 解决什么问题

在企业级 Java 应用中，JSON 是最常用的数据交换格式。Fastjson 解决了以下核心痛点：

- **高性能序列化/反序列化**：相比 JDK 原生或其他 JSON 库，Fastjson 在解析速度和内存占用上具有显著优势，适合高并发、大数据量的接口场景
- **灵活的类型转换**：支持将 JSON 字符串直接转换为 Java Bean、Map、List 等复杂对象，也支持泛型集合的反序列化
- **丰富的定制能力**：通过注解和过滤器机制，可以精确控制字段的序列化/反序列化行为（如字段重命名、忽略空值、日期格式化等）
- **与 Spring Boot 无缝集成**：可作为 Spring MVC 的 HttpMessageConverter，替代默认的 Jackson 进行请求/响应的自动 JSON 处理

在本框架中，Fastjson 2.x（2.0.53）被用作底层 JSON 工具，为统一响应封装、事件系统数据传递、脚本引擎参数绑定等模块提供高效的序列化支撑。

## 如何使用

### Maven 依赖

```xml
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.53</version>
</dependency>
```

### 核心 API

| 方法 | 说明 |
|------|------|
| `JSON.toJSONString(object)` | 将 Java 对象序列化为 JSON 字符串 |
| `JSON.parseObject(json, Class<T>)` | 将 JSON 字符串反序列化为指定类型的 Java 对象 |
| `JSON.parseArray(json, Class<T>)` | 将 JSON 数组字符串反序列化为 List |
| `JSON.toJSONBytes(object)` | 将 Java 对象序列化为 byte[]（适合网络传输/缓存存储） |
| `JSONObject.parseObject(json)` | 解析为 JSONObject（类似 Map 的动态访问） |

### 常用特性控制

通过 `JSONWriter.Feature` 和 `JSONReader.Feature` 枚举控制序列化/反序列化行为：

- `JSONWriter.Feature.WriteMapNullValue` — 序列化时保留 null 值字段
- `JSONWriter.Feature.PrettyFormat` — 格式化输出（带缩进换行）
- `JSONWriter.Feature.WriteEnumsUsingToString` — 枚举使用 toString() 而非 ordinal
- `JSONReader.Feature.SupportSmartMatch` — 智能匹配（忽略大小写和下划线差异）
- `JSONReader.Feature.IgnoreCheckClose` — 宽松解析，容忍非标准 JSON

### 注解定制

- `@JSONField(name = "...")` — 自定义 JSON 字段名
- `@JSONField(format = "yyyy-MM-dd HH:mm:ss")` — 日期格式化
- `@JSONField(serialize = false)` — 排除该字段不参与序列化
- `@JSONField(deserialize = false)` — 排除该字段不参与反序列化

## 使用实例

### 基础序列化与反序列化

```java
import com.alibaba.fastjson2.JSON;

// 序列化
User user = new User();
user.setId(1L);
user.setName("张三");
user.setAge(28);

String json = JSON.toJSONString(user);
// {"age":28,"id":1,"name":"张三"}

// 反序列化
User parsed = JSON.parseObject(json, User.class);
System.out.println(parsed.getName()); // 张三
```

### 泛型集合反序列化

```java
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import java.util.List;

String jsonArray = "[{\"id\":1,\"name\":\"张三\"},{\"id\":2,\"name\":\"李四\"}]";

// 使用 TypeReference 保留泛型信息
List<User> users = JSON.parseObject(jsonArray, new TypeReference<List<User>>() {});
System.out.println(users.size()); // 2
```

### 带特性的序列化

```java
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

User user = new User();
user.setId(1L);
user.setName(null);

// 默认不输出 null 字段
String compact = JSON.toJSONString(user);
// {"id":1}

// 保留 null 值 + 格式化输出
String pretty = JSON.toJSONString(user,
        JSONWriter.Feature.WriteMapNullValue,
        JSONWriter.Feature.PrettyFormat);
// {
//     "age":null,
//     "id":1,
//     "name":null
// }
```

### 使用注解定制字段

```java
import com.alibaba.fastjson2.annotation.JSONField;
import java.time.LocalDateTime;

public class Order {

    @JSONField(name = "order_id")
    private Long orderId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JSONField(serialize = false)
    private String internalCode; // 不参与序列化

    // getter/setter ...
}

Order order = new Order();
order.setOrderId(1001L);
order.setCreateTime(LocalDateTime.now());
order.setInternalCode("INTERNAL");

String json = JSON.toJSONString(order);
// {"createTime":"2026-06-08 14:30:00","order_id":1001}
// 注意：internalCode 未出现，orderId 被重命名为 order_id
```

### 动态 JSONObject 操作

```java
import com.alibaba.fastjson2.JSONObject;

// 从字符串解析
String json = "{\"name\":\"张三\",\"address\":{\"city\":\"杭州\",\"zip\":\"310000\"}}";
JSONObject obj = JSONObject.parseObject(json);

// 读取嵌套字段
String city = obj.getJSONObject("address").getString("city");
System.out.println(city); // 杭州

// 动态构建并输出
JSONObject result = new JSONObject();
result.put("code", 200);
result.put("message", "success");
result.put("data", obj);

System.out.println(result.toJSONString());
```
