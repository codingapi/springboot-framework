---
name: dynamic-data-query
description: 动态数据查询体系，基于 FastRepository + PageRequest/Filter/SearchRequest 自动构建 Example 或 HQL 查询
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter-data-fast
symbols:
  - FastRepository
  - DynamicRepository
  - DynamicNativeRepository
  - DynamicSQLBuilder
  - ExampleBuilder
  - BaseRepository
  - SortRepository
  - PageRequest
  - SearchRequest
  - RequestFilter
  - Filter
  - Relation
  - QueryColumns
  - MapViewResult
  - JpaQuery
  - JdbcQuery
  - ICurrentOffset
  - CurrentPageOffsetContext
  - DynamicTableGenerator
  - DynamicTableClassLoader
  - TableEntityClassBuilder
content_hash: 205a681871886afbc38ec401a4a24474e11d3ef833dbb9c66d910950404fba22
---

## 解决什么问题

后台管理系统的列表查询通常需要支持动态过滤、排序和分页。传统做法需要大量 if-else 拼接查询条件。本能力通过扩展 Spring Data JPA，提供了声明式的动态查询方案：

- **动态过滤条件**：通过 `RequestFilter` 声明式添加过滤条件，自动构建查询
- **前端参数直连**：`SearchRequest` 直接解析 HttpServletRequest 参数为 PageRequest
- **多种查询模式**：支持 Example 查询（简单过滤）和 HQL 查询（复杂关联）
- **Map 结果映射**：支持自定义列选择和 Map 结果返回

## 如何使用

### 基础分页查询

```java
// Repository 继承 FastRepository
public interface UserRepository extends FastRepository<User, Long> {}

// 分页 + 过滤
PageRequest request = PageRequest.of(0, 20);
request.addFilter("name", "张三");
request.addFilter("age", Relation.GT, 18);
request.addFilter("status", Relation.IN, "ACTIVE", "PENDING");
Page<User> page = userRepository.findAll(request);
```

### 复杂 HQL 查询

```java
// 使用 pageRequest 触发 HQL 构建（支持关联查询过滤）
PageRequest request = PageRequest.of(0, 20);
request.addFilter("dept.name", "技术部");  // 关联字段
request.addSort(Sort.by("createTime").descending());
Page<User> page = userRepository.pageRequest(request);
```

### 从 HTTP 请求自动构建

```java
// 自动解析前端传递的 filter/sort/params 参数
SearchRequest searchRequest = new SearchRequest();
searchRequest.setCurrent(0);
searchRequest.setPageSize(20);
PageRequest pageRequest = searchRequest.toPageRequest(User.class);
Page<User> page = userRepository.searchRequest(searchRequest);
```

### 支持的 Relation 操作

| Relation | 含义 | 示例 |
|----------|------|------|
| `EQUAL` | 等于 | `addFilter("name", "张三")` |
| `NOT_EQUAL` | 不等于 | `addFilter("status", Relation.NOT_EQUAL, "DELETED")` |
| `GT` / `GTE` | 大于/大于等于 | `addFilter("age", Relation.GT, 18)` |
| `LT` / `LTE` | 小于/小于等于 | `addFilter("price", Relation.LTE, 100)` |
| `LIKE` | 模糊匹配 | `addFilter("name", Relation.LIKE, "张")` |
| `IN` | 包含 | `addFilter("status", Relation.IN, "A", "B")` |

## 使用实例

```java
// Controller 层
@GetMapping("/users")
public MultiResponse<User> list() {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.setCurrent(0);
    searchRequest.setPageSize(20);
    Page<User> page = userRepository.searchRequest(searchRequest);
    return MultiResponse.of(page);
}

// 前端请求参数：
// ?current=0&pageSize=20&filter=eyJuYW1lIjpbIuW8oCJdfQ==&sort=eyJjcmVhdGVUaW1lIjoiZGVzY2VuZCJ9
// filter 和 sort 参数为 Base64 编码的 JSON
```
