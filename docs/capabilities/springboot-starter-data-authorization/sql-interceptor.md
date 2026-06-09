---
name: springboot-starter-data-authorization/sql-interceptor
module: springboot-starter-data-authorization
description: 基于 JSqlParser 的 SQL 拦截器，对 SELECT 语句递归改写以注入行级数据权限条件
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-data-authorization"
symbols:
  - DefaultSQLInterceptor
  - SQLInterceptor
  - SQLInterceptorContext
  - DataPermissionSQL
  - DataPermissionSQLEnhancer
content_hash: 4ccd229afdaba85784de3adbfe98b9c96ae342baf3f5ad7ba9dbddbeb50f603b
---

## 解决什么问题

在多租户/部门级/用户级的数据权限场景下，业务 SQL 往往只写 `SELECT * FROM order`，但实际执行时需要自动追加 `WHERE dept_id IN (...) AND create_user = ?` 之类的条件。手动在每个 DAO 中拼接条件既易遗漏又难维护。

本能力提供 `SQLInterceptor` 抽象，框架默认实现 `DefaultSQLInterceptor` 通过 `JSqlParser` 解析 SELECT 语句，递归遍历 FROM/JOIN/WHERE 中的子查询，按表名+别名调用 `RowHandler` 返回的 `Condition` 注入到对应位置。

## 如何使用

**1. 注册行级权限处理器**

```java
@Component
public class OrderRowHandler implements RowHandler {
    @Override
    public Condition handler(String sql, String tableName, String aliasName) {
        if (!"order".equalsIgnoreCase(tableName)) {
            return null;
        }
        List<IConditionSQL> conditions = new ArrayList<>();

        // 当前用户部门
        String deptId = currentUser.getDeptId();
        conditions.add(new EqualCondition(aliasName + ".dept_id", deptId));

        // 仅本人订单
        conditions.add(new OrCondition(
            new EqualCondition(aliasName + ".create_user", currentUser.getId()),
            new EqualCondition(aliasName + ".is_public", true)
        ));

        return new Condition(conditions);
    }
}
```

**2. 实现自定义拦截器（可选，覆盖默认）**

```java
@Component
@Primary
public class MySQLInterceptor implements SQLInterceptor {
    @Override
    public boolean beforeHandler(String sql) {
        return SQLUtils.isQuerySql(sql);
    }

    @Override
    public DataPermissionSQL postHandler(String sql) throws SQLException {
        // 自定义 SQL 改写逻辑
        ...
    }

    @Override
    public void afterHandler(String sql, String newSql, SQLException exception) {
        // 审计日志
    }
}
```

**3. 配置（可选）**

```yaml
codingapi:
  authorization:
    show-sql: true   # 打印改写后的 SQL
```

**4. 工作流程（见 `DataPermissionSQLEnhancer.deepMatch`）**

- 解析 SQL → `Select` AST
- 若为 `PlainSelect`：处理 FROM + JOIN + WHERE
- 若为 `SetOperationList`：递归处理每个子 SELECT
- WHERE 中的 `AndExpression/OrExpression/InExpression/ParenthesedSelect` 也递归处理
- 对每个表调用 `RowHandler.handler()` 获取 `Condition` 并通过 `ConditionSQLHandlerContext` 注入

## 使用实例

```java
// 1. 业务方编写原始 SQL，无需关心权限
@Repository
public class OrderRepository {
    @Query("SELECT * FROM order WHERE status = 'PAID'")
    List<Order> findPaidOrders();
}
// 实际执行时会被改写为：
// SELECT * FROM order o WHERE status = 'PAID' AND o.dept_id = ? AND (o.create_user = ? OR o.is_public = true)

// 2. 子查询递归处理
// 复杂报表：SELECT * FROM (SELECT * FROM order WHERE ...) tmp
// 内层 SELECT 也会被自动增强

// 3. 注册多个 RowHandler（按表名分发）
@Component
public class CompositeRowHandler implements RowHandler {
    @Autowired private OrderRowHandler orderHandler;
    @Autowired private UserRowHandler userHandler;

    @Override
    public Condition handler(String sql, String tableName, String aliasName) {
        if ("order".equalsIgnoreCase(tableName)) return orderHandler.handler(sql, tableName, aliasName);
        if ("user".equalsIgnoreCase(tableName)) return userHandler.handler(sql, tableName, aliasName);
        return null;
    }
}
```
