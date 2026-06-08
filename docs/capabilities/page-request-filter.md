---
name: page-request-filter
description: 动态查询过滤请求封装，支持多种比较关系、组合条件、HTTP 参数自动解析
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在企业级应用的列表查询场景中，前端通常需要传递多个筛选条件。传统做法存在以下问题：

1. **Controller 参数爆炸**：每个查询字段都需要一个 `@RequestParam`，方法签名冗长
2. **条件拼接复杂**：需要手动构建 JPA Specification 或 HQL，代码重复度高
3. **前后端协议不统一**：不同项目的分页参数命名（page/pageNo/current）和过滤格式各异
4. **动态条件难维护**：新增查询字段需要同时修改 Controller、Service、Repository 三层
5. **类型转换繁琐**：字符串参数需要手动转为对应的实体字段类型

框架通过 `PageRequest` + `RequestFilter` + `Relation` 三件套提供统一的动态查询抽象。`SearchRequest` 进一步封装了 HTTP 请求参数的自动解析，支持 Base64 编码的 filter/sort/params JSON 参数，实现前端零配置对接。

## 如何使用

### 1. PageRequest — 编程式构建查询条件

```java
// 基础分页
PageRequest request = PageRequest.of(0, 20);

// 添加等值过滤
request.addFilter("name", "张三");

// 添加指定关系的过滤
request.addFilter("age", Relation.GREATER_THAN, 18);
request.addFilter("createTime", Relation.BETWEEN, startDate, endDate);
request.addFilter("status", Relation.IN, 1, 2, 3);

// 添加排序
request.addSort(Sort.by("createTime").descending());

// 链式调用
PageRequest req = PageRequest.of(0, 20)
    .addFilter("name", Relation.LIKE, "张")
    .addFilter("age", Relation.GREATER_THAN_EQUAL, 18);
```

### 2. Relation — 支持的比较关系

| 枚举值 | SQL 等价 | 说明 |
|--------|----------|------|
| `EQUAL` | `= value` | 等值匹配（默认） |
| `NOT_EQUAL` | `!= value` | 不等 |
| `LIKE` | `LIKE %value%` | 模糊匹配 |
| `LEFT_LIKE` | `LIKE %value` | 左模糊 |
| `RIGHT_LIKE` | `LIKE value%` | 右模糊 |
| `BETWEEN` | `BETWEEN v1 AND v2` | 区间 |
| `IN` | `IN (v1, v2, ...)` | 包含 |
| `NOT_IN` | `NOT IN (v1, v2, ...)` | 不包含 |
| `IS_NULL` | `IS NULL` | 为空 |
| `IS_NOT_NULL` | `IS NOT NULL` | 非空 |
| `GREATER_THAN` | `> value` | 大于 |
| `LESS_THAN` | `< value` | 小于 |
| `GREATER_THAN_EQUAL` | `>= value` | 大于等于 |
| `LESS_THAN_EQUAL` | `<= value` | 小于等于 |

### 3. Filter — 组合条件

使用 `Filter.and()` 和 `Filter.or()` 构建复合条件：

```java
// AND 组合：name LIKE '%张%' AND age > 18
request.andFilter(
    Filter.as("name", Relation.LIKE, "张"),
    Filter.as("age", Relation.GREATER_THAN, 18)
);

// OR 组合：status = 1 OR status = 2
request.orFilters(
    Filter.as("status", Relation.EQUAL, 1),
    Filter.as("status", Relation.EQUAL, 2)
);
```

### 4. SearchRequest — HTTP 参数自动解析

在 Controller 中直接使用 `SearchRequest`，自动从 HTTP 请求中提取分页和过滤参数：

```java
@GetMapping("/users")
public MultiResponse<UserDTO> list(SearchRequest searchRequest) {
    // 传入实体类用于类型推断
    PageRequest pageRequest = searchRequest.toPageRequest(User.class);
    Page<User> page = userRepository.pageRequest(pageRequest);
    return MultiResponse.of(page);
}
```

**前端传参方式：**

- 直接参数：`?current=0&pageSize=20&name=张三&age=18`
- Base64 编码 filter：`?filter=eyJuYW1lIjpbIuW8oCJdfQ==`（解码后为 `{"name":["张"]}` ）
- Base64 编码 sort：`?sort=eyJjcmVhdGVUaW1lIjoiZGVzY2VuZCJ9`
- Base64 编码 params（指定操作符）：`?params=W3sia2V5IjoiYWdlIiwidHlwZSI6IkdSRUFURVJfVEhBTiJ9XQ==`

### 5. RequestFilter — 读取已设置的过滤条件

```java
// 检查是否有过滤条件
if (request.hasFilter()) { ... }

// 获取特定过滤值
String name = request.getStringFilter("name");
int age = request.getIntFilter("age", 0); // 带默认值

// 移除某个过滤条件
request.removeFilter("name");
```

## 使用实例

### 完整的用户列表查询

```java
// Entity
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private LocalDateTime createTime;
    private Integer status;
}

// Repository
public interface UserRepository extends FastRepository<User, Long> {
}

// Controller
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 方式一：编程式构建
     */
    @GetMapping("/search")
    public MultiResponse<User> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(defaultValue = "0") int current,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        PageRequest request = PageRequest.of(current, pageSize)
            .addSort(Sort.by("createTime").descending());
        
        if (StringUtils.hasText(name)) {
            request.addFilter("name", Relation.LIKE, name);
        }
        if (minAge != null) {
            request.addFilter("age", Relation.GREATER_THAN_EQUAL, minAge);
        }
        
        Page<User> page = userRepository.findAll(request);
        return MultiResponse.of(page);
    }

    /**
     * 方式二：SearchRequest 自动解析
     */
    @GetMapping
    public MultiResponse<User> list(SearchRequest searchRequest) {
        PageRequest pageRequest = searchRequest.toPageRequest(User.class);
        Page<User> page = userRepository.searchRequest(searchRequest);
        return MultiResponse.of(page);
    }
}
```

### 复杂组合查询示例

```java
// 查询：(name LIKE '%张%' AND age >= 18) OR status = 1
PageRequest request = PageRequest.of(0, 20);

request.orFilters(
    Filter.and(
        Filter.as("name", Relation.LIKE, "张"),
        Filter.as("age", Relation.GREATER_THAN_EQUAL, 18)
    ),
    Filter.as("status", Relation.EQUAL, 1)
);

Page<User> page = userRepository.pageRequest(request);
```

### Filter 工具方法速查

```java
// 快捷创建
Filter.as("key", value)              // EQUAL
Filter.as("key", Relation.LIKE, val) // 指定关系
Filter.and(f1, f2, ...)             // AND 组合
Filter.or(f1, f2, ...)              // OR 组合

// 判断类型
filter.isEqual()
filter.isLike()
filter.isBetween()
filter.isIn()
filter.isAndFilters()
filter.isOrFilters()

// 获取值（自动类型转换）
Object val = filter.getFilterValue(Integer.class);
```
