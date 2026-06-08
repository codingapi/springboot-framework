---
name: springboot-starter-data-fast/dynamic-query-convention
module: springboot-starter-data-fast
description: 动态查询规范，定义 PageRequest + RequestFilter + FastRepository 的使用模式
status: 已实现
scope: 后端
source: 框架:springboot-starter-data-fast
import: "com.codingapi.springboot:springboot-starter-data-fast"
framework_version: "3.4.54"
---

## 解决什么问题

在企业级应用中，列表查询往往需要根据前端传入的动态条件进行过滤。如果不遵守本规范，会导致以下问题：

1. **SQL 注入风险**：在 Service 层手动拼接 SQL 字符串实现动态查询，极易引入 SQL 注入漏洞。
2. **代码重复与维护困难**：每个实体的动态查询都手写 `if-else` 判断和原生 SQL，导致大量重复代码，新增过滤字段需修改多处。
3. **分页逻辑不一致**：各模块自行处理分页参数（偏移量计算、排序），缺乏统一标准，容易出现边界错误。
4. **CQRS 职责混乱**：查询逻辑与命令逻辑耦合在同一 Service 中，违反读写分离原则，增加系统复杂度。
5. **类型安全缺失**：使用 Map 或 JSON 传递查询条件时，缺乏编译期检查，运行时才发现字段名拼写错误或类型不匹配。
6. **框架能力浪费**：不使用 `FastRepository` 提供的自动 Example/HQL 构建能力，绕过框架自建查询机制，失去统一的 SQL 拦截（如数据权限）支持。

## 如何使用

### 核心规则

1. **列表查询统一使用 `PageRequest` 构建分页参数**
   - 通过 `PageRequest.of(page, size)` 或 `new PageRequest()` 创建请求对象。
   - 禁止直接使用 Spring Data 原生 `org.springframework.data.domain.PageRequest` 来承载业务过滤条件。

2. **动态过滤条件通过 `PageRequest.addFilter()` 方法添加**
   - 简单等值过滤：`pageRequest.addFilter("name", "张三")`，默认使用 `Relation.EQ`。
   - 指定关系过滤：`pageRequest.addFilter("age", Relation.GT, 18)`。
   - 组合过滤：使用 `andFilter(Filter...)` 和 `orFilters(Filter...)` 构建复杂条件。

3. **过滤关系使用 `Relation` 枚举**
   - 可用关系包括：`EQ`、`GT`、`LT`、`GTE`、`LTE`、`LIKE`、`IN` 等。
   - 所有过滤关系必须通过枚举表达，禁止硬编码字符串比较运算符。

4. **Repository 接口需继承 `FastRepository`**
   - 实体 Repository 必须扩展 `FastRepository<T, ID>` 以获得动态查询能力。
   - `FastRepository` 同时继承了 `JpaRepository`、`JpaSpecificationExecutor`、`DynamicRepository` 和 `DynamicNativeRepository`。

5. **调用 `FastRepository.findAll(PageRequest)` 执行动态查询**
   - 当 `PageRequest` 包含 Filter 时，自动通过 `ExampleBuilder` 构建 Example 查询。
   - 需要 HQL 级别查询时，使用 `pageRequest(PageRequest)` 方法，自动通过 `DynamicSQLBuilder` 生成 HQL。
   - 禁止绕过 `FastRepository` 提供的默认方法自行编写动态查询逻辑。

6. **禁止在 Service 层手写原生 SQL 实现动态查询**
   - 不允许使用 `@Query(nativeQuery = true)` 或 `EntityManager.createNativeQuery()` 拼接动态条件。
   - 所有动态过滤必须委托给 `FastRepository` 的 Filter 机制完成。

7. **查询服务（CQRS Query 侧）应独立于命令服务**
   - 查询服务放在 `*-app-query` 模块中，仅负责读取操作。
   - 命令服务放在 `*-app-cmd-*` 模块中，负责写入与领域编排。
   - 两者不得互相依赖或合并为同一个类。

### 命名约定

- 查询服务类命名：`{Entity}QueryService`，位于 `*.query` 包下。
- 命令服务类命名：`{Entity}CmdService`，位于 `*.cmd` 包下。
- Controller 中的列表查询方法参数类型统一为 `PageRequest`。

## 使用实例

### ✅ 正确示例

```java
// 1. Repository 继承 FastRepository
public interface UserRepository extends FastRepository<User, Long> {
}

// 2. 查询服务独立于命令服务
@Service
public class UserQueryService {

    @Autowired
    private UserRepository userRepository;

    public Page<User> listUsers(String name, Integer minAge, int page, int size) {
        PageRequest request = PageRequest.of(page, size);

        // 动态添加过滤条件
        if (StringUtils.hasText(name)) {
            request.addFilter("name", Relation.LIKE, name);
        }
        if (minAge != null) {
            request.addFilter("age", Relation.GTE, minAge);
        }

        // 委托 FastRepository 自动构建查询
        return userRepository.findAll(request);
    }
}

// 3. Controller 接收 PageRequest
@GetMapping("/users")
public MultiResponse<User> list(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer minAge,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    Page<User> result = userQueryService.listUsers(name, minAge, page, size);
    return ResponseUtils.toMultiResponse(result);
}
```

### ❌ 错误示例

```java
// 错误 1: Repository 未继承 FastRepository，丧失动态查询能力
public interface UserRepository extends JpaRepository<User, Long> {
}

// 错误 2: Service 中手写原生 SQL 拼接动态条件
@Service
public class UserService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> listUsers(String name, Integer minAge) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        if (name != null) {
            sql.append(" AND name LIKE '%").append(name).append("%'");  // SQL 注入风险！
        }
        if (minAge != null) {
            sql.append(" AND age >= ").append(minAge);
        }
        return entityManager.createNativeQuery(sql.toString(), User.class).getResultList();
    }
}

// 错误 3: 查询与命令逻辑混合在同一个 Service 中
@Service
public class UserService {
    public Page<User> list(...) { /* 查询逻辑 */ }
    public void createUser(...) { /* 命令逻辑 */ }
    public void deleteUser(...) { /* 命令逻辑 */ }
}

// 错误 4: 使用 Spring Data 原生 PageRequest，无法承载 Filter
@GetMapping("/users")
public Page<User> list(org.springframework.data.domain.PageRequest pageable) {
    // 丢失了框架的动态过滤能力
    return userRepository.findAll(pageable);
}
```
