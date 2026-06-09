---
name: springboot-starter-data-fast/jpa-repository
module: springboot-starter-data-fast
description: 增强版 JPA Repository 接口体系，支持动态 SQL、原生查询、Map 返回、分页、排序与高级搜索
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-data-fast"
symbols:
  - BaseRepository
  - DynamicRepository
  - DynamicNativeRepository
  - SortRepository
  - FastRepository
content_hash: 25a2f36d9e6b8884a84feef23916c16218218e1b4162221779651c051d7c31d5
---

## 解决什么问题

标准 Spring Data JPA 的 `JpaRepository` 在面对以下场景时力不从心：

- 动态 SQL（运行时拼接 WHERE 条件）
- 原生 SQL（避免 HQL/Criteria 复杂度）
- 返回 `Map`/`MapViewResult`（前端表格、报表）
- 列表+分页+动态过滤的统一抽象
- 拖拽排序（基于 `ISort.sort` 字段）

本能力在 `JpaRepository` 之上扩展了 5 个接口，按需组合使用：

- `BaseRepository`：提供 `getEntityClass()` 反射工具
- `DynamicRepository`：基于 HQL 的动态查询
- `DynamicNativeRepository`：基于原生 SQL 的查询
- `SortRepository`：拖拽排序
- `FastRepository`：聚合上述所有能力 + 高级搜索（`PageRequest`/`SearchRequest`）

## 如何使用

**1. 基础使用（推荐 `FastRepository`）**

```java
public interface UserRepository extends FastRepository<User, Long> {
    // 0 行业务代码，直接继承获得所有能力
}
```

**2. 动态列表查询**

```java
// 方式 1：传 SQL + params
List<User> users = userRepository.dynamicListQuery(
    "FROM User u WHERE u.deptId = ?1 AND u.status = ?2", deptId, "ACTIVE");

// 方式 2：传 SQLBuilder（链式）
SQLBuilder<User> builder = SQLBuilder.of(User.class)
    .where("u.deptId = :dept")
    .setParam("dept", deptId);
List<User> users = userRepository.dynamicListQuery(builder);

// 方式 3：返回 Map
List<MapViewResult> rows = userRepository.dynamicMapListQuery(
    QueryColumns.of("id", "name", "phone"),
    "SELECT id, name, phone FROM user WHERE dept_id = ?1", deptId);
```

**3. 动态分页**

```java
PageRequest request = PageRequest.of(0, 20, Sort.by("createTime").descending());
Page<User> page = userRepository.dynamicPageQuery(builder, request);
```

**4. 高级搜索（FastRepository 特有）**

```java
// 接收前端的 PageRequest（含 filter / sort / page）
Page<User> page = userRepository.findAll(pageRequest);

// 或 SearchRequest（含 keyword）
Page<User> page = userRepository.searchRequest(searchRequest);
```

**5. 拖拽排序**

```java
// 实体实现 ISort 接口（含 sort 字段）
SortRequest request = new SortRequest(Arrays.asList(10L, 8L, 6L, 4L, 2L));
userRepository.reSort(request);  // 重新分配 sort 值
```

**6. 配合 JpaQueryContext 与 JdbcQueryContext**

- `DynamicRepository` → `JpaQueryContext.getJpaQuery().listQuery(...)`（基于 HQL）
- `DynamicNativeRepository` → `JdbcQueryContext.getJdbcQuery().queryForList(...)`（基于 JDBC）

可通过自定义 `JpaQueryConfiguration` / `JdbcQueryConfiguration` 覆盖默认实现。

## 使用实例

```java
// 1. 业务仓库示例
public interface OrderRepository extends FastRepository<Order, Long> {
}

// 2. 报表场景：返回 Map 分页
@GetMapping("/report/orders")
public Page<MapViewResult> report(OrderQuery query, PageRequest request) {
    QueryColumns columns = QueryColumns.of("id", "title", "amount", "status", "createTime");
    String sql = "SELECT id, title, amount, status, create_time FROM order WHERE 1=1 ";
    if (query.getStatus() != null) sql += " AND status = ?";
    return orderRepository.dynamicMapPageQuery(columns, sql, countSql, request, query.getStatus());
}

// 3. 配合搜索请求体
public class UserSearchRequest extends SearchRequest {
    private String keyword;
    private Long deptId;
    // getters/setters
}
Page<User> page = userRepository.searchRequest(userSearchRequest);
```
