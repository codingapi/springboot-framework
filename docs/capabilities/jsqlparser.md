---
name: jsqlparser
description: SQL 解析与改写库，用于数据权限 SQL 注入
status: 已实现
scope: 后端
source: 框架:JSqlParser
---

## 解决什么问题

JSqlParser 为项目的数据权限模块提供 SQL 解析和改写能力，解决了以下问题：

- **SQL 解析**：将原始 SQL 字符串解析为结构化的 AST（抽象语法树），理解表名、字段、WHERE 条件等
- **SQL 改写**：在 SELECT 语句中自动注入数据权限过滤条件，实现透明的行级数据隔离
- **零侵入**：业务代码无需修改，权限条件通过 JDBC 代理层自动注入

## 如何使用

项目使用 JSqlParser 5.0，仅在 `springboot-starter-data-authorization` 模块中使用：

1. **SQL 解析**：通过 `CCJSqlParserUtil.parse(sql)` 将 SQL 解析为 `Statement` 对象
2. **条件注入**：`DataPermissionSQLEnhancer` 遍历 SELECT 语句，根据 `RowHandler` 定义的权限规则向 WHERE 子句追加条件
3. **代理链拦截**：`ConnectionProxy` → `PreparedStatementProxy` / `StatementProxy` 在 SQL 执行前调用 `SQLRunningContext.intercept(sql)` 完成改写
4. **跳过拦截**：通过 `SQLRunningContext.skipDataAuthorization()` 可临时跳过权限过滤

## 使用实例

### DataPermissionSQLEnhancer 核心逻辑

```java
public class DataPermissionSQLEnhancer {

    public DataPermissionSQLEnhancer(String sql, RowHandler rowHandler) throws SQLException {
        this.sql = sql.replaceAll("\\?", " ? ");
        this.rowHandler = rowHandler;
        // 使用 JSqlParser 解析 SQL
        this.statement = CCJSqlParserUtil.parse(this.sql);
        this.tableColumnAliasHolder = new TableColumnAliasHolder(statement);
    }

    public String getNewSQL() throws SQLException {
        if (statement instanceof Select) {
            tableColumnAliasHolder.holderAlias();
            Select select = (Select) statement;
            this.deepMatch(select);       // 递归注入权限条件
            return statement.toString();   // 返回改写后的 SQL
        }
        return sql;
    }
}
```

### ConnectionProxy 中的拦截点

```java
public class ConnectionProxy implements Connection {

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        // 在创建 PreparedStatement 前拦截并改写 SQL
        this.interceptState = SQLRunningContext.getInstance().intercept(sql);
        return new PreparedStatementProxy(
            connection.prepareStatement(interceptState.getSql()),
            interceptState
        );
    }
}
```

### 临时跳过数据权限

```java
// 某些管理操作需要查询全量数据，跳过权限过滤
Object result = SQLRunningContext.getInstance()
    .skipDataAuthorization(() -> {
        return userRepository.findAll();
    });
```
