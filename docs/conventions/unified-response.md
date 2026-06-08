---
name: unified-response
description: 所有 API 返回值必须使用统一响应封装（Response → SingleResponse / MultiResponse / MapResponse），禁止直接返回裸对象
status: 已实现
scope: 后端
source: 项目自有
last_commit: 841c49b8
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/dto/response/Response.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/dto/response/SingleResponse.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/dto/response/MultiResponse.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/dto/response/MapResponse.java
---

## 解决什么问题

如果 Controller 直接返回裸对象（如 `UserEntity`、`List<UserEntity>`、`Map`），会导致以下问题：

1. **前后端协议不一致**：不同接口返回结构各异，前端无法统一解析成功/失败状态
2. **错误信息缺失**：裸对象不包含 `errCode` / `errMessage`，异常处理与正常响应格式不统一
3. **分页元数据丢失**：直接返回 `List` 时缺少 `total` 等分页信息，前端无法正确渲染分页组件
4. **扩展困难**：后续如需增加请求 ID、时间戳等通用字段，需逐个修改所有接口

## 如何使用

框架提供四种响应类型，均继承自 `Response` 基类：

| 类型 | 用途 | 工厂方法 |
|------|------|----------|
| `Response` | 无数据的操作结果（仅 success/errCode/errMessage） | `Response.buildSuccess()` / `Response.buildFailure(errCode, errMessage)` |
| `SingleResponse<T>` | 返回单个对象 | `SingleResponse.of(data)` / `SingleResponse.empty()` |
| `MultiResponse<T>` | 返回列表（支持分页） | `MultiResponse.of(collection, total)` / `MultiResponse.of(page)` / `MultiResponse.of(collection)` / `MultiResponse.empty()` |
| `MapResponse` | 返回键值对数据 | `MapResponse.create().add(key, value)` / `MapResponse.empty()` |

### 规则

1. **Controller 方法的返回类型必须是上述四种之一**，不得返回实体类、集合或 Map 裸对象
2. 成功时使用 `of()` / `buildSuccess()` 等静态工厂方法创建响应
3. 失败时优先通过抛出 `LocaleMessageException` 由全局异常处理器自动构建错误响应；仅在非异常场景下手动调用 `Response.buildFailure()`
4. `MultiResponse` 接收 Spring Data `Page` 对象时，使用 `MultiResponse.of(page)` 自动提取 `content` 和 `totalElements`

## 使用实例

### ✅ 正确示例

```java
@RestController
@RequestMapping("/api/users")
public class UserQueryController {

    @Autowired
    private UserQueryService userQueryService;

    // 返回单个对象
    @GetMapping("/{id}")
    public SingleResponse<UserEntity> getById(@PathVariable Long id) {
        return SingleResponse.of(userQueryService.getById(id));
    }

    // 返回列表（带分页）
    @PostMapping("/list")
    public MultiResponse<UserEntity> list(@RequestBody SearchRequest searchRequest) {
        return MultiResponse.of(userQueryService.list(searchRequest));
    }

    // 返回键值对
    @GetMapping("/stats")
    public MapResponse stats() {
        return MapResponse.create()
                .add("totalUsers", 100)
                .add("activeUsers", 85);
    }

    // 无数据操作
    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        userCommandService.delete(id);
        return Response.buildSuccess();
    }
}
```

### ❌ 错误示例

```java
@RestController
@RequestMapping("/api/users")
public class UserQueryController {

    // 错误：直接返回实体类
    @GetMapping("/{id}")
    public UserEntity getById(@PathVariable Long id) {
        return userQueryService.getById(id);
    }

    // 错误：直接返回 List，缺少 total 和统一包装
    @GetMapping("/list")
    public List<UserEntity> list() {
        return userQueryService.listAll();
    }

    // 错误：直接返回 Map
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalUsers", 100);
        return map;
    }

    // 错误：使用 ResponseEntity 包装裸对象
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userCommandService.delete(id);
        return ResponseEntity.ok().build();
    }
}
```
