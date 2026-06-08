---
name: springboot-starter/response-dto
module: springboot-starter
description: 统一响应封装，提供 Response/SingleResponse/MultiResponse/MapResponse 四种响应类型
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在企业级 REST API 开发中，前后端交互需要统一的响应契约。若每个接口各自定义返回结构，会导致以下问题：

- **前端解析复杂**：不同接口的成功/失败字段命名不一致，前端需逐接口适配
- **错误处理分散**：缺乏统一的 `errCode` / `errMessage` 规范，全局异常拦截难以标准化
- **分页结构不统一**：列表接口的总数、数据字段名各异，通用分页组件无法复用
- **序列化行为不可控**：响应对象在缓存、消息队列等场景下需要可靠的 JSON 序列化能力

`response-dto` 提供了四种标准响应类型，覆盖无数据、单对象、列表/分页、键值对四类返回场景，使所有 API 输出遵循同一契约。基类 `Response` 实现了 `JsonSerializable` 接口，支持通过 Fastjson 进行一致的 JSON 序列化。

## 如何使用

### 类层次结构

```
JsonSerializable (interface)
  └── Response                    — 基础响应（success / errCode / errMessage）
        ├── SingleResponse<T>     — 单对象响应，data 字段为泛型 T
        ├── MultiResponse<T>      — 列表响应，data 字段包含 total + list
        └── MapResponse           — 键值对响应，data 字段为 Map<String, Object>
```

### 核心 API

| 类 | 静态工厂方法 | 说明 |
|---|---|---|
| `Response` | `buildSuccess()` | 构建成功响应（无业务数据） |
| `Response` | `buildFailure(errCode, errMessage)` | 构建失败响应 |
| `SingleResponse<T>` | `of(T data)` | 包装单个业务对象 |
| `SingleResponse<T>` | `empty()` | 返回 data=null 的成功响应 |
| `MultiResponse<T>` | `of(Collection<T> data, long total)` | 包装集合 + 总数 |
| `MultiResponse<T>` | `of(Page<T> page)` | 直接包装 Spring Data Page 对象 |
| `MultiResponse<T>` | `of(Collection<T> data)` | 包装集合，total 自动取 size |
| `MultiResponse<T>` | `empty()` | 返回空列表的成功响应 |
| `MapResponse` | `create()` | 创建空的键值对响应 |
| `MapResponse` | `empty()` | 返回 data=null 的键值对响应 |
| `MapResponse` | `add(key, value)` | 链式添加键值对 |

### JSON 序列化

所有响应类均继承自 `Response`，而 `Response` 实现了 `JsonSerializable` 接口，可直接调用 `toJson()` 方法获取 JSON 字符串：

```java
String json = response.toJson(); // 使用 Fastjson 序列化
```

### Maven 依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter</artifactId>
</dependency>
```

## 使用实例

### 1. 无数据的操作响应

```java
@PostMapping("/save")
public Response save(@RequestBody UserRequest request) {
    userService.save(request);
    return Response.buildSuccess();
}
```

返回 JSON：
```json
{ "success": true, "errCode": null, "errMessage": null }
```

### 2. 返回单个对象

```java
@GetMapping("/detail")
public SingleResponse<UserVO> detail(@RequestParam Long id) {
    UserVO user = userQueryService.getById(id);
    return SingleResponse.of(user);
}
```

返回 JSON：
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": { "id": 1, "name": "张三", "email": "zhangsan@example.com" }
}
```

### 3. 返回列表（含分页）

```java
@GetMapping("/list")
public MultiResponse<UserVO> list(SearchRequest searchRequest) {
    Page<UserVO> page = userQueryService.page(searchRequest);
    return MultiResponse.of(page);
}
```

返回 JSON：
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": {
    "total": 100,
    "list": [
      { "id": 1, "name": "张三" },
      { "id": 2, "name": "李四" }
    ]
  }
}
```

### 4. 返回键值对数据

```java
@GetMapping("/dashboard/stats")
public MapResponse dashboardStats() {
    return MapResponse.create()
        .add("userCount", 1024)
        .add("orderCount", 5678)
        .add("todayRevenue", 99800.50);
}
```

返回 JSON：
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": {
    "userCount": 1024,
    "orderCount": 5678,
    "todayRevenue": 99800.50
  }
}
```

### 5. 返回错误响应

```java
@PostMapping("/login")
public Response login(@RequestBody LoginRequest request) {
    if (!userService.authenticate(request)) {
        return Response.buildFailure("AUTH_FAILED", "用户名或密码错误");
    }
    return Response.buildSuccess();
}
```

返回 JSON：
```json
{ "success": false, "errCode": "AUTH_FAILED", "errMessage": "用户名或密码错误" }
```
