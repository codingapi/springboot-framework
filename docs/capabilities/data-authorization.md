---
name: data-authorization
description: 数据权限 SQL 拦截器，通过 JDBC 代理链在 SQL 执行前透明注入权限条件，支持行级过滤与列级脱敏
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter-data-authorization
symbols:
  - ConnectionProxy
  - PreparedStatementProxy
  - StatementProxy
  - CallableStatementProxy
  - ResultSetProxy
  - SQLRunningContext
  - SQLInterceptor
  - DefaultSQLInterceptor
  - SQLInterceptorContext
  - SQLExecuteState
  - DataPermissionSQL
  - DataAuthorizationContext
  - DataAuthorizationFilter
  - DefaultDataAuthorizationFilter
  - AuthorizationJdbcDriver
  - DataPermissionSQLEnhancer
  - ColumnHandlerContext
  - ColumnMask
  - RowHandler
  - WhereConditionSQL
  - JoinConditionSQL
content_hash: 213024d85431e425f67a13790847dc638a8d99a30b1013a7992236c3d1dadf8f
---

## 解决什么问题

企业系统中，不同用户只能查看权限范围内的数据（如部门经理只看本部门数据）。传统做法需要在每个查询中手动拼接权限条件，代码侵入性大。本能力通过 JDBC 代理层实现透明的 SQL 拦截与改写，解决以下问题：

- **透明权限注入**：在 SQL 执行前自动注入 WHERE/JOIN 条件，业务代码无感知
- **行级数据过滤**：根据用户权限动态追加行过滤条件
- **列级数据脱敏**：通过 ColumnMask 对敏感字段（手机号、身份证、银行卡）进行脱敏
- **跳过权限控制**：提供 `skipDataAuthorization()` 方法在特定场景下绕过权限拦截

## 如何使用

### 配置数据权限过滤器

```java
@Bean
public DataAuthorizationFilter dataAuthorizationFilter() {
    return (tableName, aliasContext) -> {
        if ("sys_user".equals(tableName)) {
            return new WhereConditionSQL("dept_id", 
                Relation.IN, getCurrentUserDeptIds());
        }
        return null; // 不过滤
    };
}
```

### 配置 SQL 拦截器

```java
@Bean
public SQLInterceptor sqlInterceptor() {
    return new DefaultSQLInterceptor(dataAuthorizationFilter);
}
```

### 使用 JDBC 驱动代理

将数据库驱动替换为 `AuthorizationJdbcDriver`，所有通过 JDBC 执行的 SQL 将自动经过权限拦截。

### 跳过权限拦截

```java
// 在特定查询中跳过数据权限
Object result = SQLRunningContext.getInstance()
    .skipDataAuthorization(() -> {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM sys_user", Long.class);
    });
```

## 使用实例

```java
// 业务代码正常写查询，无需关心权限
Page<User> users = userRepository.findAll(PageRequest.of(0, 20));
// SQL: SELECT * FROM sys_user LIMIT 20
// 实际执行: SELECT * FROM sys_user WHERE dept_id IN (1,2,3) LIMIT 20

// 列脱敏配置
@Bean
public ColumnMask phoneMask() {
    return new PhoneMask(); // 138****8888
}
```
