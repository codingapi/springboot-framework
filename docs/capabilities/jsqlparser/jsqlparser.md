---
name: jsqlparser/jsqlparser
module: jsqlparser
description: JSqlParser SQL 解析器，支持 SQL 语句解析和改写
status: 已实现
scope: 后端
source: 框架:jsqlparser
import: "com.github.jsqlparser:jsqlparser"
framework_version: 5.0
---

## 解决什么问题

在企业级应用中，经常需要在运行时对 SQL 语句进行动态分析和改写，典型场景包括：

- **数据权限过滤**：在 SQL 执行前透明注入行级权限条件（如 `WHERE dept_id IN (...)`），无需修改业务代码
- **SQL 审计与监控**：解析 SQL 结构以提取表名、操作类型等元信息
- **多租户隔离**：自动为查询追加租户过滤条件
- **SQL 合法性校验**：判断传入的 SQL 是否为合法的 SELECT 查询，防止误操作

JSqlParser 提供了完整的 SQL 语法树解析能力，使上述场景可以在不依赖特定数据库方言的前提下，以统一的方式对 SQL 进行结构化访问和改写。在本框架中，它作为 `springboot-starter-data-authorization` 模块的核心依赖，支撑了数据权限 SQL 增强器的实现。

## 如何使用

### Maven 依赖

```xml
<dependency>
    <groupId>com.github.jsqlparser</groupId>
    <artifactId>jsqlparser</artifactId>
    <version>5.0</version>
</dependency>
```

### 核心 API

| 类 / 接口 | 说明 |
|-----------|------|
| `CCJSqlParserUtil.parse(sql)` | 将 SQL 字符串解析为 `Statement` 语法树对象 |
| `Statement` | SQL 语句的顶层抽象，可向下转型为 `Select`、`Insert`、`Update`、`Delete` 等 |
| `Select` / `PlainSelect` | 查询语句节点，提供 `getFromItem()`、`getWhere()`、`getJoins()` 等方法访问子句 |
| `Expression` | WHERE / ON 条件表达式树，支持 `AndExpression`、`OrExpression`、`InExpression` 等 |
| `Table` | 表引用节点，包含表名和别名信息 |
| `Join` | JOIN 子句节点，可通过 `getRightItem()` 获取关联表或子查询 |
| `statement.toString()` | 将修改后的语法树重新序列化为 SQL 字符串 |

### 典型使用流程

1. 调用 `CCJSqlParserUtil.parse(sql)` 获得 `Statement` 对象
2. 通过 `instanceof` 判断语句类型并向下转型
3. 遍历语法树节点，读取或修改表名、条件、JOIN 等信息
4. 调用 `toString()` 输出改写后的 SQL

## 使用实例

### 示例 1：判断 SQL 是否为 SELECT 查询

```java
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

public class SQLUtils {

    public static boolean isQuerySql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            return statement instanceof Select;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 示例 2：解析 SQL 并向 WHERE 子句注入权限条件

以下示例展示了框架中 `DataPermissionSQLEnhancer` 的核心思路——解析 SQL 后遍历所有表和 JOIN，按需追加数据权限过滤条件：

```java
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

public class DataPermissionExample {

    public String enhanceWithPermission(String sql, Expression permissionCondition) throws Exception {
        // 1. 解析 SQL
        Statement statement = CCJSqlParserUtil.parse(sql);

        if (statement instanceof Select) {
            PlainSelect plainSelect = ((Select) statement).getPlainSelect();

            // 2. 获取原有 WHERE 条件
            Expression existingWhere = plainSelect.getWhere();

            // 3. 用 AND 拼接权限条件
            if (existingWhere != null) {
                plainSelect.setWhere(new AndExpression(existingWhere, permissionCondition));
            } else {
                plainSelect.setWhere(permissionCondition);
            }

            // 4. 处理 JOIN 中的表（类似逻辑，此处省略）
        }

        // 5. 返回改写后的 SQL
        return statement.toString();
    }
}
```

### 示例 3：递归处理子查询和 UNION

对于包含子查询或 `UNION` 的复杂 SQL，需要递归遍历语法树以确保权限条件被注入到每一层查询中：

```java
private void deepMatch(Select select) throws Exception {
    if (select instanceof PlainSelect) {
        PlainSelect plainSelect = select.getPlainSelect();
        enhanceDataPermissionInSelect(plainSelect);
    }
    if (select instanceof SetOperationList) {
        SetOperationList setOperationList = select.getSetOperationList();
        for (Select subSelect : setOperationList.getSelects()) {
            deepMatch(subSelect.getPlainSelect());
        }
    }
}
```

> **注意**：JSqlParser 5.0 对 API 进行了部分调整（如 `ParenthesedSelect` 替代旧版 `SubSelect`），升级时请参考官方迁移指南。
