---
name: unified-response-convention
description: API 统一响应返回规范
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

统一的响应格式解决了以下问题：

- **前后端契约**：所有 API 返回结构一致，前端可以用统一的拦截器处理成功/失败
- **错误标准化**：错误信息包含 `errCode` + `errMessage`，支持国际化和本地化展示
- **类型安全**：通过泛型 Response 子类明确每个接口的返回数据类型
- **分页统一**：列表接口自带 `total` 字段，无需额外约定分页元数据格式

## 如何使用

### Response 体系

```java
// 基础响应 —— 仅表示成功/失败
public class Response {
    boolean success;
    String errCode;
    String errMessage;

    static Response buildSuccess();
    static Response buildFailure(String errCode, String errMessage);
}

// 单对象响应
public class SingleResponse<T> extends Response {
    T data;
    static <T> SingleResponse<T> of(T data);
    static <T> SingleResponse<T> empty();
}

// 列表响应（含分页）
public class MultiResponse<T> extends Response {
    Content<T> data;  // { total: long, list: Collection<T> }
    static <T> MultiResponse<T> of(Collection<T> data);
    static <T> MultiResponse<T> of(Collection<T> data, long total);
    static <T> MultiResponse<T> of(Page<T> page);
    static <T> MultiResponse<T> empty();
}

// Map 响应
public class MapResponse extends Response {
    Map<String, Object> data;
    static MapResponse create();
    MapResponse add(String key, Object value);
}
```

### 使用规则

| 场景 | 返回类型 | 工厂方法 |
|------|---------|---------|
| 无返回数据的操作 | `Response` | `Response.buildSuccess()` |
| 返回单个对象 | `SingleResponse<T>` | `SingleResponse.of(data)` |
| 返回列表/分页 | `MultiResponse<T>` | `MultiResponse.of(list)` / `MultiResponse.of(page)` |
| 返回键值对 | `MapResponse` | `MapResponse.create().add(k, v)` |
| 业务错误 | `Response` | `Response.buildFailure(code, msg)` 或抛出 `LocaleMessageException` |

## 使用实例

### ✅ 正确示例：命令接口返回 Response

```java
@PostMapping("/save")
public Response save(@RequestBody UserCmd.UpdateRequest request) {
    userRouter.createOrUpdate(request);
    return Response.buildSuccess();
}

@PostMapping("/remove")
public Response remove(@RequestBody IdRequest request) {
    userRouter.removeUser(request.getLongId());
    return Response.buildSuccess();
}
```

### ✅ 正确示例：查询接口返回 MultiResponse

```java
@GetMapping("/list")
public MultiResponse<UserEntity> list(SearchRequest searchRequest) {
    return MultiResponse.of(userQueryService.list(searchRequest));
}
```

### ✅ 正确示例：返回单个对象用 SingleResponse

```java
@GetMapping("/detail")
public SingleResponse<FlowWork> getFlowStep(@RequestBody IdRequest request) {
    FlowWork work = flowAppQueryService.getFlowStep(request.getLongId());
    return SingleResponse.of(work);
}
```

### ✅ 正确示例：返回 Map 数据

```java
@GetMapping("/stats")
public MapResponse stats() {
    return MapResponse.create()
        .add("totalUsers", userService.count())
        .add("activeUsers", userService.countActive());
}
```

### ❌ 错误示例：直接返回 Entity 而非 Response 包装

```java
@GetMapping("/user")
public User getUser(@RequestParam long id) {
    // 禁止！缺少 success/errCode/errMessage 标准字段
    return userService.getById(id);
}
```

### ❌ 错误示例：手动构造 JSON 字符串

```java
@GetMapping("/list")
public String list() {
    // 禁止！绕过 Response 体系，前端无法统一处理
    return "{\"success\":true,\"data\":[...]}";
}
```

### ❌ 错误示例：用 null 代替正确的空响应

```java
@GetMapping("/detail")
public SingleResponse<User> detail(@RequestParam long id) {
    User user = userService.getById(id);
    if (user == null) {
        return null;  // 禁止！应使用 SingleResponse.empty()
    }
    return SingleResponse.of(user);
}
```

### ❌ 错误示例：在 Controller 中捕获异常并手动构建错误响应

```java
@PostMapping("/save")
public Response save(@RequestBody UserCmd cmd) {
    try {
        userRouter.createOrUpdate(cmd);
        return Response.buildSuccess();
    } catch (Exception e) {
        // 禁止！应由全局异常处理器统一处理
        return Response.buildFailure("error", e.getMessage());
    }
}
```
