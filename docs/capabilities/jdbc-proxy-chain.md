---
name: jdbc-proxy-chain
description: 完整的 JDBC 装饰器代理链，透明拦截 SQL 执行以实现数据权限控制
status: 已实现
scope: 后端
source: 项目自有
last_commit: 67895fda
code_files:
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/jdbc/proxy/ConnectionProxy.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/jdbc/proxy/StatementProxy.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/jdbc/proxy/PreparedStatementProxy.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/jdbc/proxy/CallableStatementProxy.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/jdbc/proxy/ResultSetProxy.java
---

## 解决什么问题

数据权限要求在不修改业务 SQL 的前提下，透明地注入行级/列级过滤条件。手动在每个 Repository 中拼接权限条件会导致：
- 业务代码与权限逻辑强耦合
- 遗漏某个查询导致数据泄露
- 难以统一管理和审计权限规则

JDBC 代理链通过装饰器模式包装整个 JDBC 调用链，在 SQL 执行前自动拦截和改写，对业务代码完全透明。

## 如何使用

### 代理链架构

```
ConnectionProxy（包装 DataSource 返回的 Connection）
    │
    ├── createStatement() → StatementProxy
    │       └── executeQuery(sql) → SQLRunningContext.intercept(sql) → ResultSetProxy
    │
    ├── prepareStatement(sql) → SQLRunningContext.intercept(sql) → PreparedStatementProxy
    │       └── executeQuery() → ResultSetProxy
    │
    └── prepareCall(sql) → SQLRunningContext.intercept(sql) → CallableStatementProxy
            └── executeQuery() → ResultSetProxy
```

### 各代理职责

| 代理类 | 职责 |
|--------|------|
| `ConnectionProxy` | 拦截 `prepareStatement`/`prepareCall`，在 SQL 创建时调用 `SQLRunningContext.intercept()` |
| `StatementProxy` | 拦截 `executeQuery`/`executeUpdate`，动态改写 SQL 并包装返回值 |
| `PreparedStatementProxy` | 包装已拦截的 PreparedStatement，对返回的 ResultSet 应用列级权限 |
| `CallableStatementProxy` | 委托底层 CallableStatement，应用授权上下文 |
| `ResultSetProxy` | 拦截列级数据读取，通过 `ColumnHandlerContext` 对字段值进行脱敏或过滤 |

### 拦截流程

1. `ConnectionProxy.prepareStatement(sql)` 调用 `SQLRunningContext.getInstance().intercept(sql)`
2. `SQLRunningContext` 返回 `SQLExecuteState`（包含改写后的 SQL 和拦截状态）
3. 改写后的 SQL 传给底层 `Connection.prepareStatement()`
4. 返回的 `ResultSet` 被 `ResultSetProxy` 包装
5. 读取每列时，`ColumnHandlerContext` 根据表名和列名决定是否脱敏

### 自动装配

代理链通过 `DataAuthorizationConfiguration` 自动注入到 DataSource 中，业务代码无需感知：

```java
// 框架自动将 DataSource.getConnection() 替换为 ConnectionProxy
// 所有 JPA/JDBC 查询自动走代理链
```

## 使用实例

```java
// 自定义 SQL 拦截器 — 为特定表注入租户条件
public class TenantSQLInterceptor implements SQLInterceptor {
    @Override
    public SQLExecuteState intercept(String sql) {
        if (sql.contains("FROM orders")) {
            String tenantId = UserContext.getCurrentUser().getTenantId();
            String modifiedSql = sql + " AND tenant_id = '" + tenantId + "'";
            return new SQLExecuteState(modifiedSql, true);
        }
        return new SQLExecuteState(sql, false);
    }
}

// 自定义列级处理器 — 对手机号脱敏
public class PhoneColumnHandler implements ColumnHandler {
    @Override
    public String handle(String tableName, String columnName, String value) {
        if ("user".equals(tableName) && "phone".equals(columnName)) {
            return value.substring(0, 3) + "****" + value.substring(7);
        }
        return value;
    }
}
```
