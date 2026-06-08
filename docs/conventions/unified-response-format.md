---
name: unified-response-format
description: 统一响应格式规范 — 所有 API 必须使用 Response/SingleResponse/MultiResponse 标准封装返回
status: 已实现
scope: 后端
source: 项目自有
symbols:
  - Response
  - SingleResponse
  - MultiResponse
  - MapResponse
content_hash: 1dfe2559a6b514effb51735ca8ca49aba308959e02814c5862b842eed3b04fb1
---

## 解决什么问题

不遵守此规范会导致：
- 前端需要针对不同的 API 返回格式做多种适配
- 错误响应格式不一致，前端无法统一处理错误提示
- 分页数据结构不统一，增加前端列表组件的复杂度

## 如何使用

### 响应类型选择规则

| 场景 | 使用类型 | 说明 |
|------|----------|------|
| 无返回数据的操作 | `Response` | 创建/更新/删除操作 |
| 返回单个对象 | `SingleResponse<T>` | 详情查询、单条记录 |
| 返回列表/分页 | `MultiResponse<T>` | 列表查询、分页查询 |
| 返回 Map 结构 | `MapResponse` | 键值对数据 |

### 分页数据格式

分页数据必须使用 `MultiResponse.of(Page<T>)` 构建，结构为：
```json
{
  "success": true,
  "data": {
    "total": 100,
    "list": [...]
  }
}
```

### 错误响应格式

```json
{
  "success": false,
  "errCode": "业务错误码（英文点分隔）",
  "errMessage": "用户可读的错误描述"
}
```

## 使用实例

✅ **正确示例**：
```java
@GetMapping("/{id}")
public SingleResponse<User> get(@PathVariable Long id) {
    return SingleResponse.of(userService.findById(id));
}

@GetMapping
public MultiResponse<User> list() {
    Page<User> page = userRepository.findAll(PageRequest.of(0, 20));
    return MultiResponse.of(page);
}

@PostMapping
public Response create(@RequestBody UserDTO dto) {
    userService.create(dto);
    return Response.buildSuccess();
}
```

❌ **错误示例**：
```java
// 直接返回实体
@GetMapping("/{id}")
public User get(@PathVariable Long id) {
    return userService.findById(id);
}

// 自定义 Map 返回
@GetMapping
public Map<String, Object> list() {
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("data", users);
    return result;
}
```
