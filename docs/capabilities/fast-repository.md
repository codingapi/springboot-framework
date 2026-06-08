---
name: fast-repository
description: JPA Repository 增强，支持基于 PageRequest 的动态过滤查询、HQL 构建和 SearchRequest 自动解析
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

Spring Data JPA 的 `JpaRepository` 提供了基础的 CRUD 操作，但在实际业务中存在以下不足：

1. **动态条件查询繁琐**：需要手动编写 `Specification` 或 `@Query`，每个查询场景都要新建方法
2. **前端过滤条件无法直传**：前端传来的 JSON 过滤参数需要逐字段解析再拼装查询
3. **Example 查询局限**：Spring Data Example 只支持等值和部分模糊匹配，不支持 BETWEEN、IN、大于小于等关系
4. **分页与过滤割裂**：分页参数和过滤条件分散在不同对象中，缺乏统一封装
5. **原生 SQL 构建困难**：复杂动态查询需要手写字符串拼接，容易出错且不安全

`FastRepository` 扩展了 `JpaRepository` + `JpaSpecificationExecutor`，内置三种查询模式：
- `findAll(PageRequest)` — 使用 Spring Data Example 进行简单等值/模糊查询
- `pageRequest(PageRequest)` — 使用 HQL 动态构建，支持全部 `Relation` 关系
- `searchRequest(SearchRequest)` — 直接从 HTTP 请求参数解析并执行查询

## 如何使用

### 1. 定义 Repository 接口

只需继承 `FastRepository<T, ID>`，无需额外代码：

```java
public interface UserRepository extends FastRepository<User, Long> {
    // 自动获得 findAll(PageRequest)、pageRequest(PageRequest)、searchRequest(SearchRequest) 等方法
}
```

### 2. 使用 findAll(PageRequest) — Example 模式

适用于简单的等值和 LIKE 查询，底层使用 Spring Data Example：

```java
PageRequest request = PageRequest.of(0, 20);
request.addFilter("name", "张三");      // EQUAL
request.addFilter("email", Relation.LIKE, "@example.com");

Page<User> page = userRepository.findAll(request);
```

> **注意**：Example 模式仅支持有限的 Relation 类型。如需完整关系支持，请使用 `pageRequest()` 方法。

### 3. 使用 pageRequest(PageRequest) — HQL 模式

支持所有 `Relation` 枚举定义的比较关系，底层动态生成 HQL：

```java
PageRequest request = PageRequest.of(0, 20)
    .addFilter("age", Relation.GREATER_THAN, 18)
    .addFilter("createTime", Relation.BETWEEN, startDate, endDate)
    .addFilter("status", Relation.IN, 1, 2, 3)
    .addSort(Sort.by("createTime").descending());

Page<User> page = userRepository.pageRequest(request);
```

### 4. 使用 searchRequest(SearchRequest) — HTTP 自动解析模式

直接将 HTTP 请求参数转为查询条件，适合前后端联调：

```java
@GetMapping("/users")
public MultiResponse<User> list(SearchRequest searchRequest) {
    Page<User> page = userRepository.searchRequest(searchRequest);
    return MultiResponse.of(page);
}
```

前端可通过 URL 参数传递过滤条件：
```
GET /api/users?current=0&pageSize=20&name=张三&age=18
```

或通过 Base64 编码的 filter/sort 参数传递复杂条件。

### 5. 结合 DynamicNativeRepository

`FastRepository` 同时继承了 `DynamicNativeRepository`，支持原生 SQL 动态查询（具体用法参见相关文档）。

## 使用实例

### 完整的用户管理 CRUD + 动态查询

```java
// Entity
@Entity
@Table(name = "t_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private Integer age;
    private Integer status;
    private LocalDateTime createTime;
}

// Repository
public interface UserRepository extends FastRepository<User, Long> {
    // 可继续添加自定义查询方法
    List<User> findByStatus(Integer status);
}

// Service
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 简单等值查询
     */
    public Page<User> findByName(String name, int page, int size) {
        PageRequest request = PageRequest.of(page, size);
        request.addFilter("name", name);
        return userRepository.findAll(request);
    }

    /**
     * 复杂条件查询
     */
    public Page<User> advancedSearch(UserSearchCriteria criteria) {
        PageRequest request = PageRequest.of(criteria.getPage(), criteria.getSize())
            .addSort(Sort.by("createTime").descending());

        if (StringUtils.hasText(criteria.getName())) {
            request.addFilter("name", Relation.LIKE, criteria.getName());
        }
        if (criteria.getMinAge() != null) {
            request.addFilter("age", Relation.GREATER_THAN_EQUAL, criteria.getMinAge());
        }
        if (criteria.getMaxAge() != null) {
            request.addFilter("age", Relation.LESS_THAN_EQUAL, criteria.getMaxAge());
        }
        if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            request.addFilter("status", Relation.IN, 
                criteria.getStatuses().toArray());
        }
        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            request.addFilter("createTime", Relation.BETWEEN, 
                criteria.getStartDate(), criteria.getEndDate());
        }

        return userRepository.pageRequest(request);
    }

    /**
     * HTTP 参数自动解析查询
     */
    public Page<User> searchFromHttp(SearchRequest searchRequest) {
        return userRepository.searchRequest(searchRequest);
    }
}

// Controller
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/simple")
    public MultiResponse<User> simpleSearch(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<User> result = userService.findByName(name, page, size);
        return MultiResponse.of(result);
    }

    @GetMapping("/advanced")
    public MultiResponse<User> advancedSearch(UserSearchCriteria criteria) {
        Page<User> result = userService.advancedSearch(criteria);
        return MultiResponse.of(result);
    }

    @GetMapping
    public MultiResponse<User> autoSearch(SearchRequest searchRequest) {
        Page<User> result = userService.searchFromHttp(searchRequest);
        return MultiResponse.of(result);
    }
}
```

### 三种查询模式对比

| 特性 | `findAll(PageRequest)` | `pageRequest(PageRequest)` | `searchRequest(SearchRequest)` |
|------|------------------------|----------------------------|--------------------------------|
| 底层实现 | Spring Data Example | 动态 HQL | 转 PageRequest → HQL |
| 支持的 Relation | EQUAL, LIKE (有限) | 全部 14 种 | 全部 14 种 |
| 组合条件 (AND/OR) | ❌ | ✅ | ✅ |
| HTTP 参数自动解析 | ❌ | ❌ | ✅ |
| 嵌套属性查询 | 有限支持 | ✅ | ✅ |
| 性能 | 较好 | 良好 | 良好 |
| 适用场景 | 简单等值筛选 | 复杂业务查询 | 前后端联调/通用列表页 |

### 注意事项

1. **实体类要求**：过滤字段名必须与实体类的属性名一致（不是数据库列名）
2. **类型匹配**：过滤值的类型应与实体字段类型兼容，框架会尝试自动转换
3. **空值处理**：当过滤值为 null 或空字符串时，该条件会被跳过
4. **SQL 注入防护**：HQL 模式使用参数化查询，不存在 SQL 注入风险
5. **分页起始页**：`PageRequest.of(page, size)` 中 page 从 0 开始
