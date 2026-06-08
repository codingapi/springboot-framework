---
name: fast-repository
description: JPA Repository 增强，支持动态过滤查询（RequestFilter + Relation 操作符）、HQL 自动构建、排序
status: 已实现
scope: 后端
source: 项目自有
last_commit: 44ba20df
code_files:
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/jpa/repository/FastRepository.java
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/jpa/repository/BaseRepository.java
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/jpa/repository/DynamicRepository.java
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/jpa/repository/SortRepository.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/dto/request/PageRequest.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/dto/request/RequestFilter.java
---

## 解决什么问题

Spring Data JPA 原生的 `JpaRepository` 在处理复杂查询时存在以下痛点：

- **动态条件拼接繁琐**：需要手动编写 `Specification` 或 `@Query`，无法根据前端传入的过滤参数自动构建查询
- **分页与过滤分离**：Spring 原生 `PageRequest` 不支持携带过滤条件，业务代码需要在 Controller/Service 层反复组装查询逻辑
- **缺少丰富的比较操作符**：LIKE、BETWEEN、IN、IS_NULL 等常用操作符没有统一的抽象

本能力通过扩展 `PageRequest` 和提供 `FastRepository` 接口，实现了：

- `RequestFilter` + `Relation` 枚举统一表达 14 种查询操作符
- `FastRepository.findAll(PageRequest)` 自动根据 Filter 构建 Example 查询
- `FastRepository.pageRequest(PageRequest)` 自动构建 HQL 动态查询
- `DynamicRepository` 支持原生 SQL / SQLBuilder 的动态列表和分页查询
- `SortRepository` 提供拖拽排序的 `reSort()` 方法

## 如何使用

### 核心接口

| 接口/类 | 说明 |
|---------|------|
| `FastRepository<T, ID>` | 增强型 Repository，继承 JpaRepository + JpaSpecificationExecutor + DynamicRepository |
| `BaseRepository<T, ID>` | 基础 Repository，提供 `getEntityClass()` 反射获取泛型类型 |
| `DynamicRepository<T, ID>` | 动态查询 Repository，支持 SQLBuilder 和原生 SQL 查询 |
| `SortRepository<T extends ISort, ID>` | 排序 Repository，提供 `reSort(SortRequest)` 拖拽排序 |
| `PageRequest` | 扩展 Spring Data PageRequest，内置 `RequestFilter` |
| `RequestFilter` | 过滤条件容器，管理多个 `Filter` |
| `Filter` | 单个过滤条件，包含 key、relation、value |
| `Relation` | 查询操作符枚举 |

### Relation 操作符

```
EQUAL, NOT_EQUAL, LIKE, LEFT_LIKE, RIGHT_LIKE,
BETWEEN, IN, NOT_IN, IS_NULL, IS_NOT_NULL,
GREATER_THAN, LESS_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL
```

### 注入方式

让实体 Repository 继承 `FastRepository` 即可：

```java
public interface UserRepository extends FastRepository<User, Long> {
}
```

## 使用实例

### 1. 基础动态过滤查询（Example 模式）

```java
// 创建分页请求并添加过滤条件
PageRequest request = PageRequest.of(0, 20);
request.addFilter("name", "张三");                    // 默认 EQUAL
request.addFilter("age", Relation.GREATER_THAN, 18);   // 大于
request.addFilter("email", Relation.LIKE, "gmail");    // 模糊匹配

Page<User> page = userRepository.findAll(request);
```

### 2. HQL 动态查询

```java
PageRequest request = PageRequest.of(0, 20);
request.addFilter("status", Relation.IN, "ACTIVE", "PENDING");
request.addFilter("createTime", Relation.BETWEEN, startDate, endDate);

// pageRequest() 使用 HQL 构建查询，适合复杂条件
Page<User> page = userRepository.pageRequest(request);
```

### 3. AND/OR 组合条件

```java
PageRequest request = PageRequest.of(0, 20);

// OR 组合：name='张三' OR name='李四'
request.orFilters(
    Filter.as("name", "张三"),
    Filter.as("name", "李四")
);

// AND 组合
request.andFilters(
    Filter.as("age", Relation.GREATER_THAN_EQUAL, 18),
    Filter.as("status", "ACTIVE")
);
```

### 4. 从请求中读取过滤值

```java
// 在 Service 层获取前端传入的过滤参数
String name = request.getStringFilter("name", "default");
int age = request.getIntFilter("age", 0);
boolean hasFilter = request.hasFilter();
```

### 5. 原生 SQL 动态查询

```java
// 使用 DynamicRepository 的原生 SQL 查询
List<User> users = userRepository.dynamicListQuery(
    "SELECT * FROM user WHERE status = ?", "ACTIVE"
);

// 分页查询
Page<User> page = userRepository.dynamicPageQuery(
    "SELECT * FROM user WHERE dept_id = ?",
    "SELECT COUNT(*) FROM user WHERE dept_id = ?",
    PageRequest.of(0, 20),
    deptId
);
```

### 6. 拖拽排序

```java
// 实体需实现 ISort 接口
SortRequest sortRequest = new SortRequest(List.of(3L, 1L, 2L));
userRepository.reSort(sortRequest);
```
