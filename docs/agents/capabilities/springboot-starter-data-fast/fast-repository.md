---
name: springboot-starter-data-fast/fast-repository
module: springboot-starter-data-fast
description: JPA 增强 Repository，支持 PageRequest 动态过滤查询和 HQL 构建
status: 已实现
scope: 后端
source: 框架:springboot-starter-data-fast
import: "com.codingapi.springboot:springboot-starter-data-fast"
framework_version: "17.3.0-SNAPSHOT"
---

## 解决什么问题

在标准 Spring Data JPA 开发中，动态条件查询通常需要手动编写 `Specification`、`QueryDSL` 或拼接 HQL，代码冗长且难以维护。当业务列表页面需要支持多字段组合筛选（等于、模糊、范围、IN 等）时，开发者往往要为每种查询场景编写独立的 Repository 方法或复杂的 Specification 构建逻辑。

`FastRepository` 通过扩展 `JpaRepository` 和 `JpaSpecificationExecutor`，提供了基于 `PageRequest` 的声明式动态查询能力：

- **findAll → Example/HQL 自动切换**：`findAll(PageRequest)` 有 Filter 条件时，全部为简单等值条件则构建 Spring Data `Example` 查询；包含 LIKE/范围/IN/OR 等复杂条件时自动切换参数化 HQL，零额外代码
- **pageRequest → HQL 动态构建**：需要 LIKE、范围、IN、OR 等复杂条件时，必须显式调用 `pageRequest(PageRequest)`，由 `DynamicSQLBuilder` 构建参数化 HQL，避免 SQL 注入风险
- **SearchRequest 集成**：支持从 HTTP 请求参数中自动解析 filter、sort 条件，适用于前端列表页的通用查询接口
- **OR/AND 组合过滤**：支持嵌套的 OR/AND 条件组合，满足复杂业务筛选需求

## 如何使用

### 1. 定义 Repository 接口

继承 `FastRepository<T, ID>` 即可获得全部动态查询能力：

```java
public interface UserEntityRepository extends FastRepository<UserEntity, Long> {
    // 标准 JpaRepository 方法仍然可用
    UserEntity getUserEntityByUsername(String username);
}
```

### 2. 使用 PageRequest 进行动态过滤查询

```java
// 创建分页请求并添加等值过滤条件
PageRequest request = PageRequest.of(0, 20);
request.addFilter("name", "张三");              // 等值匹配（默认 EQUAL）
request.addFilter("status", "active");          // 等值匹配

// 方式一：findAll —— 等值条件走 Example 查询，复杂条件自动切换 HQL
Page<UserEntity> page = repository.findAll(request);

// 方式二：pageRequest —— 需要 LIKE/范围/IN/OR 等复杂条件时必须显式调用，走 HQL
PageRequest hqlRequest = PageRequest.of(0, 20);
hqlRequest.addFilter("age", Relation.GREATER_THAN, 18);       // 大于
hqlRequest.addFilter("email", Relation.LIKE, "gmail");        // 模糊查询
Page<UserEntity> page2 = repository.pageRequest(hqlRequest);
```

### 3. 支持的过滤关系（Relation）

| Relation | 说明 | HQL 示例 |
|----------|------|----------|
| EQUAL（默认） | 等于 | `name = ?1` |
| NOT_EQUAL | 不等于 | `name != ?1` |
| GREATER_THAN | 大于 | `age > ?1` |
| LESS_THAN | 小于 | `age < ?1` |
| GREATER_THAN_EQUAL | 大于等于 | `age >= ?1` |
| LESS_THAN_EQUAL | 小于等于 | `age <= ?1` |
| LIKE | 全模糊 | `name LIKE ?1`（自动加 `%value%`） |
| LEFT_LIKE | 左模糊 | `name LIKE ?1`（自动加 `%value`） |
| RIGHT_LIKE | 右模糊 | `name LIKE ?1`（自动加 `value%`） |
| IN | 包含 | `id IN (?1)` |
| NOT_IN | 不包含 | `id NOT IN (?1)` |
| BETWEEN | 区间 | `age BETWEEN ?1 AND ?2` |
| IS_NULL | 为空 | `name IS NULL` |
| IS_NOT_NULL | 非空 | `name IS NOT NULL` |

> 注意：以上 Relation 在 `pageRequest()` / `searchRequest()` 中完整生效；`findAll()` 会按条件类型自动选择——全部等值条件走 Example 查询，包含 LIKE/范围/IN/OR 等复杂条件时自动切换 HQL。

### 4. 使用 SearchRequest 从 HTTP 请求自动解析

```java
// 在 Controller 中使用 SearchRequest，自动从 URL 参数解析 filter 和 sort
@GetMapping("/users")
public MultiResponse<UserEntity> list() {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.addFilter("status", "active"); // 追加服务端固定条件
    Page<UserEntity> page = userRepository.searchRequest(searchRequest);
    return MultiResponse.of(page.getContent());
}
```

前端通过 URL 参数传递动态条件：
- `?filter=eyJuYW1lIjpbIuW8oCJdfQ==`（Base64 编码的 JSON：`{"name":["张"]}`)
- `?sort=eyJjcmVhdGVUaW1lIjoiZGVzY2VuZCJ9`（Base64 编码的 JSON：`{"createTime":"descend"}`)

### 5. OR / AND 组合条件

```java
PageRequest request = PageRequest.of(0, 20);

// OR 条件：name = '张三' OR name = '李四'
request.orFilters(
    new Filter("name", "张三"),
    new Filter("name", "李四")
);

// AND 条件组
request.andFilter(
    new Filter("age", Relation.GREATER_THAN_EQUAL, 18),
    new Filter("status", "active")
);
```

## 使用实例

### 完整示例：用户列表查询

```java
@Service
public class UserQueryService {

    @Resource
    private UserEntityRepository userRepository;

    /**
     * 基础动态查询 - 包含 LIKE/范围条件，显式使用 pageRequest 走 HQL
     */
    public Page<UserEntity> findUsers(String name, Integer minAge, String status) {
        PageRequest request = PageRequest.of(0, 20);
        if (name != null) {
            request.addFilter("name", Relation.LIKE, name);
        }
        if (minAge != null) {
            request.addFilter("age", Relation.GREATER_THAN_EQUAL, minAge);
        }
        if (status != null) {
            request.addFilter("status", status);
        }
        return userRepository.pageRequest(request);
    }

    /**
     * 复杂 HQL 查询 - 带排序
     */
    public Page<UserEntity> findActiveUsersWithSort() {
        PageRequest request = PageRequest.of(0, 20, Sort.by("createTime").descending());
        request.addFilter("status", "active");
        request.addFilter("age", Relation.BETWEEN, 18, 65);
        return userRepository.pageRequest(request);
    }

    /**
     * 前端驱动的通用查询接口
     */
    public Page<UserEntity> searchFromHttpRequest() {
        SearchRequest searchRequest = new SearchRequest();
        // 追加服务端安全条件，防止越权查询
        searchRequest.addFilter("deleted", false);
        return userRepository.searchRequest(searchRequest);
    }
}
```

### 内部工作原理

`FastRepository.findAll(PageRequest)` 的执行流程：

1. 检查 `request.hasFilter()` — 无过滤条件时直接委托给 Spring Data 的标准 `findAll(PageRequest)`
2. 有过滤条件时，通过 `RequestFilter.isAllEqualFilter()` 判断是否全部为简单等值条件
3. 全部等值条件：通过 `ExampleBuilder` 构建 `Example` 对象（按实体属性名匹配 Filter，取 `value[0]` 写入实体字段），传入 `findAll(Example, Pageable)` 执行查询
4. 包含复杂条件（LIKE、范围、IN、NOT_EQUAL、IS_NULL、OR/AND 组合等）：自动转发 `pageRequest()`，由 `DynamicSQLBuilder` 构建参数化 HQL 执行查询

> 说明：`ExampleBuilder` 只处理 EQUAL 等值条件；过滤条件写入实体属性失败（如类型转换失败）时显式抛出 `IllegalStateException`，不再静默忽略。

`FastRepository.pageRequest(PageRequest)` 的执行流程：

1. 通过 `DynamicSQLBuilder` 根据 Filter 列表动态构建 HQL 语句和 COUNT 语句
2. 所有值通过参数化绑定（`?1`, `?2`...），防止 SQL 注入
3. 调用 `dynamicPageQuery(hql, countHql, request, params)` 执行分页查询
