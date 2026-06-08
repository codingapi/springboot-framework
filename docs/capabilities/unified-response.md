---
name: unified-response
description: 统一响应封装体系（Response / SingleResponse / MultiResponse / MapResponse），标准化 API 返回格式
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter
symbols:
  - Response
  - SingleResponse
  - MultiResponse
  - MapResponse
content_hash: 1dfe2559a6b514effb51735ca8ca49aba308959e02814c5862b842eed3b04fb1
---

## 解决什么问题

前后端分离项目中，API 返回格式需要统一规范，便于前端统一处理。本能力提供了标准化的响应封装：

- **统一结构**：所有 API 返回 `{success, errCode, errMessage, data}` 标准格式
- **泛型支持**：`SingleResponse<T>` 和 `MultiResponse<T>` 支持任意数据类型
- **分页集成**：`MultiResponse.of(Page<T>)` 直接从 Spring Data Page 构建响应
- **失败标准化**：`Response.buildFailure(code, message)` 统一错误响应

## 如何使用

### 成功响应

```java
// 无数据
return Response.buildSuccess();

// 单对象
return SingleResponse.of(user);

// 列表 + 总数
return MultiResponse.of(userList, totalCount);

// 从 Spring Data Page 构建
Page<User> page = userRepository.findAll(PageRequest.of(0, 20));
return MultiResponse.of(page);
```

### 失败响应

```java
return Response.buildFailure("user.not.found", "用户不存在");
```

### 响应结构

```json
// SingleResponse
{ "success": true, "errCode": null, "errMessage": null, "data": { "id": 1, "name": "张三" } }

// MultiResponse
{ "success": true, "data": { "total": 100, "list": [...] } }

// 失败响应
{ "success": false, "errCode": "user.not.found", "errMessage": "用户不存在" }
```

## 使用实例

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public SingleResponse<User> get(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new LocaleMessageException("user.not.found", "用户不存在");
        }
        return SingleResponse.of(user);
    }

    @GetMapping
    public MultiResponse<User> list() {
        Page<User> page = userRepository.findAll(
            PageRequest.of(0, 20).addFilter("status", "ACTIVE")
        );
        return MultiResponse.of(page);
    }
}
```
