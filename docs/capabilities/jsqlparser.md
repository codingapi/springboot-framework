---
name: jsqlparser
description: JSqlParser SQL 解析库 — 解析和改写 SQL 语句，用于数据权限条件注入
status: 已实现
scope: 后端
source: 框架:JSqlParser
import: com.github.jsqlparser:jsqlparser
framework_version: 5.0
---

## 解决什么问题

数据权限模块需要在 SQL 执行前动态注入权限条件（WHERE/JOIN 子句），手动拼接 SQL 既不安全也不灵活。JSqlParser 提供了 SQL 语法树解析和改写能力：

- **SQL 解析**：将 SQL 字符串解析为语法树（AST）
- **条件注入**：在 SELECT/UPDATE/DELETE 语句中注入 WHERE 条件
- **JOIN 注入**：为关联查询添加权限 JOIN 子句
- **别名管理**：处理表别名与列别名的映射关系

## 如何使用

### 解析 SQL

```java
Statement stmt = CCJSqlParserUtil.parse("SELECT * FROM users WHERE status = 'active'");
Select select = (Select) stmt;
PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
```

### 注入 WHERE 条件

```java
Expression where = CCJSqlParserUtil.parseCondExpression("dept_id IN (1, 2, 3)");
Expression existingWhere = plainSelect.getWhere();
if (existingWhere != null) {
    plainSelect.setWhere(new AndExpression(existingWhere, where));
} else {
    plainSelect.setWhere(where);
}
String newSql = plainSelect.toString();
// SELECT * FROM users WHERE status = 'active' AND dept_id IN (1, 2, 3)
```

### 本框架中的使用

`DataPermissionSQLEnhancer` 封装了 JSqlParser 的 SQL 改写逻辑，在 `SQLRunningContext.intercept()` 中自动调用。

## 使用实例

```java
// 数据权限自动改写 SQL
// 原始 SQL: SELECT * FROM orders WHERE status = 'PENDING'
// 改写后:  SELECT * FROM orders WHERE status = 'PENDING' AND dept_id IN (1,2)

// 直接调用
Statement stmt = CCJSqlParserUtil.parse(sql);
if (stmt instanceof Select) {
    // 注入权限条件
    enhancer.enhance((Select) stmt, tableName, condition);
}
String securedSql = stmt.toString();
```
