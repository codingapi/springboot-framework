---
name: unified-response
description: 统一 API 响应封装，提供 Response、SingleResponse、MultiResponse、MapResponse 四种标准响应格式
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在企业级应用中，API 响应格式不统一会导致以下问题：

1. **前端解析困难**：不同接口返回不同的 JSON 结构，前端需要针对每个接口做适配
2. **错误处理不一致**：有些接口用 HTTP 状态码表示错误，有些用业务字段，缺乏统一规范
3. **分页数据格式混乱**：列表接口的分页信息（总数、页码、列表）命名和结构各不相同
4. **国际化支持缺失**：错误消息硬编码在代码中，无法根据用户语言动态切换
5. **序列化兼容性**：不同 DTO 的 JSON 序列化行为不可预测

框架提供四种标准响应类型，所有响应都继承自 `Response` 基类，包含统一的 `success`、`errCode`、`errMessage` 字段。结合 `LocaleMessageException` 可实现错误的国际化输出。

## 如何使用

### 1. Response — 基础响应

仅表示操作成功或失败，不携带数据：

```java
// 成功响应
return Response.buildSuccess();

// 失败响应
return Response.buildFailure("USER_NOT_FOUND", "用户不存在");
```

### 2. SingleResponse\<T\> — 单对象响应

返回单个业务对象：

```java
// 返回数据
User user = userService.findById(id);
return SingleResponse.of(user);

// 返回空数据（表示查询无结果但不是错误）
return SingleResponse.empty();
```

### 3. MultiResponse\<T\> — 列表响应

返回集合数据，支持分页信息：

```java
// 从 Spring Data Page 构建（自动提取 total 和 content）
Page<User> page = userRepository.findAll(pageable);
return MultiResponse.of(page);

// 手动指定数据和总数
List<User> users = userService.search(keyword);
long total = userService.countByKeyword(keyword);
return MultiResponse.of(users, total);

// 仅传集合（total 自动设为集合大小）
return MultiResponse.of(users);

// 空列表
return MultiResponse.empty();
```

### 4. MapResponse — Map 响应

返回键值对结构的灵活数据：

```java
return MapResponse.create()
    .add("userName", "张三")
    .add("roleCount", 3)
    .add("permissions", Arrays.asList("READ", "WRITE"));
```

## 使用实例

### Controller 中的完整使用示例

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询单个用户
     */
    @GetMapping("/{id}")
    public SingleResponse<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        if (user == null) {
            // 抛出国际化异常，由全局异常处理器转为 Response
            throw LocaleMessageException.of("USER_NOT_FOUND", id);
        }
        return SingleResponse.of(user);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public MultiResponse<UserDTO> listUsers(PageRequest request) {
        Page<UserDTO> page = userService.findAll(request);
        return MultiResponse.of(page);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public SingleResponse<UserDTO> createUser(@RequestBody CreateUserCommand cmd) {
        UserDTO user = userService.create(cmd);
        return SingleResponse.of(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Response deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return Response.buildSuccess();
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public MapResponse getStats() {
        return MapResponse.create()
            .add("totalUsers", userService.count())
            .add("activeUsers", userService.countActive())
            .add("todayNewUsers", userService.countTodayNew());
    }
}
```

### 响应 JSON 结构示例

**SingleResponse 成功：**
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": {
    "id": 1,
    "name": "张三",
    "email": "zhangsan@example.com"
  }
}
```

**MultiResponse 分页：**
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": {
    "total": 100,
    "list": [
      {"id": 1, "name": "张三"},
      {"id": 2, "name": "李四"}
    ]
  }
}
```

**Response 失败：**
```json
{
  "success": false,
  "errCode": "USER_NOT_FOUND",
  "errMessage": "用户不存在"
}
```

### 与全局异常处理的配合

当 Controller 抛出 `LocaleMessageException` 时，全局异常处理器会自动将其转换为标准的 `Response` 格式：

```java
// Service 层抛出异常
if (user == null) {
    throw LocaleMessageException.of("USER_NOT_FOUND", userId);
}

// 框架自动捕获并返回：
// {"success": false, "errCode": "USER_NOT_FOUND", "errMessage": "用户不存在"}
```

### 响应类型选择指南

| 场景 | 推荐类型 | 说明 |
|------|----------|------|
| 增删改操作 | `Response` | 只需告知成功/失败 |
| 查询单个对象 | `SingleResponse<T>` | 可能为 null，用 `empty()` 表示 |
| 查询列表/分页 | `MultiResponse<T>` | 携带 total 和 list |
| 聚合统计/灵活结构 | `MapResponse` | 键值对形式 |
| 操作失败 | `Response.buildFailure()` 或抛异常 | 推荐抛 `LocaleMessageException` |
