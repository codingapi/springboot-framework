---
name: springboot-starter/response-convention
module: springboot-starter
description: 统一响应格式规范，定义 Controller 层返回值的标准格式
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在 REST API 开发中，如果不对响应格式做统一约束，会导致以下问题：

- **前端解析困难**：不同接口返回结构不一致（有的直接返回对象、有的返回 Map、有的包裹在自定义结构中），前端需要为每个接口单独适配，增加维护成本。
- **错误处理碎片化**：没有统一的 `success` / `errCode` / `errMessage` 字段，前端无法用一套逻辑判断请求是否成功、展示错误提示。
- **分页结构不统一**：列表接口各自定义分页字段名（`total` / `count` / `records` / `items`），前端分页组件难以复用。
- **API 契约不稳定**：随意返回裸 Map 或临时 DTO，字段增减无感知，容易引发线上联调故障。
- **国际化与监控缺失基础**：缺少标准化的错误码字段，后续接入国际化、链路追踪、告警分类时改造成本极高。

本规范通过强制使用框架提供的 Response 体系，确保所有 API 输出结构一致、可预测、可机器解析。

## 如何使用

### 核心规则

1. **Controller 方法返回值必须使用 Response 体系**：只能返回 `Response`、`SingleResponse<T>`、`MultiResponse<T>` 或 `MapResponse` 之一，禁止返回其他类型。
2. **单个对象返回**：使用 `SingleResponse.of(data)`。
3. **列表返回**：使用 `MultiResponse.of(list)` 或 `MultiResponse.of(page)`（接受 Spring Data `Page` 对象）。
4. **无数据操作成功返回**：使用 `Response.buildSuccess()`。
5. **失败返回**：使用 `Response.buildFailure(errCode, errMessage)`。
6. **禁止直接返回 Map 或自定义 DTO 作为 API 响应**。
7. **响应 JSON 结构固定包含**：`success`（boolean）、`errCode`（string）、`errMessage`（string）、`data`（业务数据，仅 SingleResponse/MultiResponse 携带）。

### 补充说明

- `SingleResponse.empty()` 用于查询可能为空但语义上成功的场景，返回 `{ success: true, data: null }`。
- `MultiResponse.of(collection, total)` 用于手动分页场景；`MultiResponse.of(page)` 自动从 Spring Data Page 提取 total。
- `MultiResponse.empty()` 返回空列表 `{ success: true, data: { total: 0, list: [] } }`。
- 异常处理应通过全局异常处理器统一转换为 `Response.buildFailure(...)`，Controller 内不要 try-catch 后自行拼装错误响应。

## 使用实例

### ✅ 正确示例

```java
// 1. 单对象查询
@GetMapping("/users/{id}")
public SingleResponse<UserDTO> getUser(@PathVariable Long id) {
    UserDTO user = userService.findById(id);
    return SingleResponse.of(user);
}

// 2. 分页列表查询
@GetMapping("/users")
public MultiResponse<UserDTO> listUsers(PageRequest pageRequest) {
    Page<UserDTO> page = userService.findAll(pageRequest);
    return MultiResponse.of(page);
}

// 3. 非分页列表查询
@GetMapping("/roles")
public MultiResponse<RoleDTO> listRoles() {
    List<RoleDTO> roles = roleService.findAll();
    return MultiResponse.of(roles);
}

// 4. 写操作成功（无返回数据）
@PostMapping("/users")
public Response createUser(@RequestBody CreateUserCmd cmd) {
    userService.create(cmd);
    return Response.buildSuccess();
}

// 5. 业务校验失败
@PostMapping("/orders")
public Response createOrder(@RequestBody CreateOrderCmd cmd) {
    if (cmd.getQuantity() <= 0) {
        return Response.buildFailure("INVALID_QUANTITY", "订单数量必须大于0");
    }
    orderService.create(cmd);
    return Response.buildSuccess();
}
```

正确返回的 JSON 结构：

```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": { "id": 1, "name": "张三" }
}
```

```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": {
    "total": 100,
    "list": [{ "id": 1, "name": "张三" }]
  }
}
```

### ❌ 错误示例

```java
// 错误1：直接返回实体对象
@GetMapping("/users/{id}")
public UserDTO getUser(@PathVariable Long id) {
    return userService.findById(id);
}

// 错误2：返回裸 Map
@GetMapping("/users/{id}")
public Map<String, Object> getUser(@PathVariable Long id) {
    Map<String, Object> result = new HashMap<>();
    result.put("code", 200);
    result.put("data", userService.findById(id));
    return result;
}

// 错误3：自定义响应结构
@GetMapping("/users/{id}")
public Result<UserDTO> getUser(@PathVariable Long id) {
    return new Result<>(userService.findById(id));
}

// 错误4：Controller 内 try-catch 自行拼装错误
@PostMapping("/users")
public Response createUser(@RequestBody CreateUserCmd cmd) {
    try {
        userService.create(cmd);
        return Response.buildSuccess();
    } catch (Exception e) {
        // 不应在 Controller 中捕获并手动构建错误响应
        Response resp = new Response();
        resp.setSuccess(false);
        resp.setErrMessage(e.getMessage());
        return resp;
    }
}

// 错误5：列表接口不使用 MultiResponse
@GetMapping("/users")
public List<UserDTO> listUsers() {
    return userService.findAll();
}
```

以上错误写法会导致前端无法统一解析响应、错误处理逻辑分散、API 契约不可靠等问题。
