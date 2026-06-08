---
name: springboot-starter-data-authorization/sql-interception
module: springboot-starter-data-authorization
description: SQL 拦截数据权限，通过 JDBC 代理透明注入权限条件实现行级数据过滤
status: 已实现
scope: 后端
source: 框架:springboot-starter-data-authorization
import: "com.codingapi.springboot:springboot-starter-data-authorization"
framework_version: "3.4.54"
---

## 解决什么问题

在企业级应用中，不同角色的用户只能看到自己有权限访问的数据（行级数据权限）。传统实现方式存在以下痛点：

- **侵入性强**：需要在每个 Service/Repository 方法中手动拼接权限条件，代码散落各处
- **容易遗漏**：新增查询接口时可能忘记添加权限过滤，导致数据泄露
- **维护困难**：权限规则变更时需要修改大量业务代码
- **与业务耦合**：权限逻辑与业务查询逻辑混杂，违反单一职责原则

SQL 拦截数据权限通过 JDBC 代理层实现了完全透明的权限注入：

- **零侵入**：业务代码无需任何修改，所有 SELECT 查询自动注入权限条件
- **全覆盖**：无论通过 JPA、MyBatis、原生 JDBC 还是其他 ORM 框架执行的 SQL，都会被统一拦截
- **灵活配置**：通过 `RowHandler` 接口按表名自定义权限规则，支持 WHERE 条件和 JOIN 关联两种注入模式
- **可跳过**：提供 `skipDataAuthorization()` API，允许特定场景下临时绕过权限检查
- **SQL 解析增强**：使用 JSqlParser 解析 SQL AST，精确识别表名和别名，支持子查询、UNION、JOIN 等复杂 SQL 结构

## 如何使用

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-data-authorization</artifactId>
</dependency>
```

### 2. 架构概览

整个拦截链路由以下组件构成：

```
DataSource → ConnectionProxy → PreparedStatementProxy / StatementProxy
                                        ↓
                               SQLRunningContext.intercept(sql)
                                        ↓
                               SQLInterceptor (DefaultSQLInterceptor)
                                        ↓
                               DataPermissionSQLEnhancer (JSqlParser)
                                        ↓
                               RowHandler.handler(tableName, alias)
                                        ↓
                               Condition (WHERE / JOIN 条件注入)
```

### 3. 核心组件说明

#### ConnectionProxy

JDBC `Connection` 的代理实现。在 `prepareStatement()`、`createStatement()`、`prepareCall()` 等方法中拦截 SQL，调用 `SQLRunningContext.intercept(sql)` 获取改写后的 SQL，并将 `SQLExecuteState` 传递给下游的 Statement 代理。

#### PreparedStatementProxy / StatementProxy

JDBC `PreparedStatement` 和 `Statement` 的代理实现。在执行 `executeQuery()`、`execute()` 等方法时，确保使用经过权限改写的 SQL。对于直接传入 SQL 字符串的方法（如 `executeQuery(String sql)`），会再次调用 `SQLRunningContext.intercept()` 进行拦截。查询结果通过 `ResultSetProxy` 包装返回。

#### SQLRunningContext

SQL 拦截的核心调度器（单例模式），负责：
- 从 `SQLInterceptorContext` 获取当前 `SQLInterceptor` 实例
- 通过 `ThreadLocal<Boolean> skipInterceptor` 控制是否跳过拦截
- 调用 `SQLInterceptor.beforeHandler()` 判断是否需要拦截（默认仅拦截 SELECT 语句）
- 调用 `SQLInterceptor.postHandler()` 执行 SQL 改写
- 提供 `skipDataAuthorization(Supplier/Runnable)` API 临时跳过权限检查

#### DefaultSQLInterceptor

默认的 SQL 拦截器实现，包含三个阶段的处理：
- `beforeHandler(sql)`：通过 `SQLUtils.isQuerySql()` 判断是否为查询语句，仅 SELECT 会被拦截
- `postHandler(sql)`：创建 `DataPermissionSQLEnhancer`，使用 JSqlParser 解析 SQL 并通过 `RowHandler` 获取权限条件，返回增强后的 SQL
- `afterHandler(sql, newSql, exception)`：日志记录，当配置 `showSql=true` 时输出改写后的 SQL

#### RowHandler

行级权限处理器接口，由业务方实现：

```java
public interface RowHandler {
    Condition handler(String subSql, String tableName, String tableAlias);
}
```

返回值 `Condition` 支持两种注入模式：
- **WHERE 条件**：`Condition.customCondition("dept_id IN (1,2,3)")` — 在 WHERE 子句中追加 AND 条件
- **JOIN 关联**：通过 `JoinConditionSQL` 添加 INNER/LEFT/RIGHT JOIN 关联表

### 4. 跳过数据权限

在某些管理操作或系统任务中需要绕过数据权限：

```java
// 方式一：Lambda 表达式
List<User> allUsers = SQLRunningContext.getInstance()
    .skipDataAuthorization(() -> userRepository.findAll());

// 方式二：Runnable
SQLRunningContext.getInstance()
    .skipDataAuthorization(() -> {
        reportService.generateMonthlyReport();
    });
```

## 使用实例

### 示例一：按部门过滤数据

```java
@Component
public class DeptRowHandler implements RowHandler {

    @Override
    public Condition handler(String subSql, String tableName, String tableAlias) {
        // 仅对 employee 表注入权限条件
        if ("employee".equalsIgnoreCase(tableName)) {
            List<Long> deptIds = SecurityContext.getCurrentDeptIds();
            if (deptIds == null || deptIds.isEmpty()) {
                return Condition.emptyCondition(); // 无权限，返回 null 不注入
            }
            String inClause = deptIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            return Condition.customCondition(
                String.format("%s.dept_id IN (%s)", tableAlias, inClause)
            );
        }
        // 其他表不注入权限条件
        return Condition.emptyCondition();
    }
}
```

效果：原始 SQL `SELECT * FROM employee WHERE status = 'active'` 被改写为：
```sql
SELECT * FROM employee WHERE dept_id IN (1,2,3) AND status = 'active'
```

### 示例二：通过 JOIN 关联实现跨表权限

```java
@Component
public class ProjectRowHandler implements RowHandler {

    @Override
    public Condition handler(String subSql, String tableName, String tableAlias) {
        if ("project".equalsIgnoreCase(tableName)) {
            Condition condition = new Condition();
            // 通过 JOIN 关联成员表，只查询当前用户参与的项目
            JoinConditionSQL joinSQL = new JoinConditionSQL(
                "project_member pm",
                JoinConditionSQL.Type.INNER,
                String.format("pm.project_id = %s.id AND pm.user_id = %d",
                    tableAlias, SecurityContext.getCurrentUserId())
            );
            condition.addConditionSQL(joinSQL);
            return condition;
        }
        return Condition.emptyCondition();
    }
}
```

效果：原始 SQL `SELECT * FROM project WHERE status = 'open'` 被改写为：
```sql
SELECT * FROM project
INNER JOIN project_member pm ON pm.project_id = project.id AND pm.user_id = 1001
WHERE status = 'open'
```

### 示例三：自定义 SQLInterceptor

如需替换默认的拦截逻辑（例如增加缓存或审计），可实现 `SQLInterceptor` 接口并注册为 Spring Bean：

```java
@Component
public class AuditSQLInterceptor implements SQLInterceptor {

    @Override
    public boolean beforeHandler(String sql) {
        // 仅拦截 SELECT 且不包含系统表的查询
        return SQLUtils.isQuerySql(sql) && !sql.contains("sys_config");
    }

    @Override
    public DataPermissionSQL postHandler(String sql) throws SQLException {
        RowHandler rowHandler = RowHandlerContext.getInstance().getRowHandler();
        DataPermissionSQLEnhancer enhancer = new DataPermissionSQLEnhancer(sql, rowHandler);
        return new DataPermissionSQL(sql, enhancer.getNewSQL(), enhancer.getTableAlias());
    }

    @Override
    public void afterHandler(String sql, String newSql, SQLException exception) {
        // 记录审计日志
        AuditLog.record(sql, newSql, exception);
    }
}
```

### 内部工作原理

1. **连接代理**：DataSource 返回的 `Connection` 被包装为 `ConnectionProxy`
2. **SQL 拦截时机**：当调用 `connection.prepareStatement(sql)` 时，`ConnectionProxy` 立即调用 `SQLRunningContext.intercept(sql)` 对 SQL 进行改写
3. **递归解析**：`DataPermissionSQLEnhancer` 使用 JSqlParser 解析 SQL AST，深度遍历 PlainSelect、SetOperationList（UNION）、子查询、JOIN 中的子 Select，对每个涉及的表调用 `RowHandler`
4. **条件注入**：`WhereConditionSQLHandler` 将 WHERE 条件通过 AND 拼接到原有 WHERE 子句；`JoinConditionSQLHandler` 向 FROM 子句追加 JOIN 关联
5. **防重入**：`SQLRunningContext` 使用 ThreadLocal 标记，在拦截器内部执行的查询不会被二次拦截
