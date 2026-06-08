---
name: sql-interceptor
description: 基于 JSqlParser 的 SQL 解析改写拦截器，在 SQL 执行前透明注入数据权限条件
status: 已实现
scope: 后端
source: 项目自有
last_commit: 67895fda
code_files:
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/interceptor/SQLInterceptor.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/interceptor/SQLRunningContext.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/interceptor/SQLInterceptorContext.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/interceptor/DefaultSQLInterceptor.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/register/SQLInterceptorRegister.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/filter/DataAuthorizationFilter.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/filter/DefaultDataAuthorizationFilter.java
---

## 解决什么问题

在企业级多租户或行级数据权限场景中，业务查询需要根据当前用户的角色、部门、数据范围等条件自动追加 WHERE 过滤条件。如果由每个开发者手动拼接权限 SQL，容易出现遗漏、不一致和安全漏洞。

本能力通过 JDBC Connection/Statement 代理层拦截所有 SQL 执行，利用 JSqlParser 解析 SQL AST，在不修改任何业务代码的前提下，自动向 SELECT 语句注入行级和列级数据权限条件。核心优势：

- **零侵入**：业务代码无需感知权限逻辑，SQL 改写对上层完全透明
- **可扩展**：通过 `SQLInterceptor` 接口自定义拦截策略，通过 `DataAuthorizationFilter` 定义具体的权限规则
- **防递归**：拦截器内部执行的查询自动跳过拦截，避免无限循环
- **支持复杂 SQL**：处理子查询、JOIN、UNION、IN 子句中的嵌套 SELECT 等场景

## 如何使用

### 核心接口

#### SQLInterceptor — SQL 拦截器接口

```java
public interface SQLInterceptor {
    // 前置判断：是否需要拦截该 SQL（默认仅拦截查询语句）
    boolean beforeHandler(String sql);

    // 核心处理：解析并改写 SQL，返回 DataPermissionSQL（含新 SQL 和表别名上下文）
    DataPermissionSQL postHandler(String sql) throws SQLException;

    // 后置回调：用于日志记录或异常处理
    void afterHandler(String sql, String newSql, SQLException exception);
}
```

#### DataAuthorizationFilter — 数据权限过滤器接口

```java
public interface DataAuthorizationFilter {
    // 行级权限：返回需要追加到 WHERE/JOIN 的条件
    Condition rowAuthorization(String tableName, String tableAlias);

    // 列级权限：对查询结果中的特定列值进行脱敏/过滤
    <T> T columnAuthorization(String tableName, String columnName, T value);

    // 是否支持对该表/列进行权限过滤
    boolean supportRowAuthorization(String tableName, String tableAlias);
    boolean supportColumnAuthorization(String tableName, String columnName, Object value);
}
```

#### SQLRunningContext — SQL 执行上下文（单例）

```java
// 获取单例
SQLRunningContext.getInstance()

// 拦截 SQL 并返回执行状态（包含改写后的 SQL）
SQLExecuteState intercept(String sql)

// 临时跳过数据权限拦截执行代码块
<T> T skipDataAuthorization(Supplier<T> supplier)
void skipDataAuthorization(Runnable runnable)
```

### 自定义拦截器注册

实现 `SQLInterceptor` 接口后，通过 Spring Bean 方式注册：

```java
@Bean
public SQLInterceptorRegister sqlInterceptorRegister(MyCustomSQLInterceptor interceptor) {
    return new SQLInterceptorRegister(interceptor);
}
```

`SQLInterceptorRegister` 会在构造时将自定义拦截器设置到 `SQLInterceptorContext` 中，替换默认的 `DefaultSQLInterceptor`。

### 自定义数据权限过滤器

实现 `DataAuthorizationFilter` 接口，并通过 `DataAuthorizationContext.getInstance().addDataAuthorizationFilter(filter)` 注册。可注册多个过滤器，按注册顺序依次匹配。

### 配置项

在 `application.properties` 中开启 SQL 日志输出：

```properties
# 开启后会在 afterHandler 中打印改写后的 SQL
codingapi.authorization.show-sql=true
```

## 使用实例

### 1. 实现行级数据权限过滤器

```java
@Component
public class DepartmentDataFilter implements DataAuthorizationFilter {

    @Override
    public boolean supportRowAuthorization(String tableName, String tableAlias) {
        // 仅对 employee 表生效
        return "employee".equalsIgnoreCase(tableName);
    }

    @Override
    public Condition rowAuthorization(String tableName, String tableAlias) {
        // 根据当前用户部门追加过滤条件
        Long deptId = SecurityContext.getCurrentDeptId();
        return Condition.formatCondition("%s.dept_id = %d", tableAlias, deptId);
    }

    @Override
    public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
        return false;
    }

    @Override
    public <T> T columnAuthorization(String tableName, String columnName, T value) {
        return value;
    }
}
```

### 2. 临时跳过数据权限拦截

```java
// 在管理后台导出全量数据时，跳过行级权限
List<Employee> allEmployees = SQLRunningContext.getInstance()
    .skipDataAuthorization(() -> employeeRepository.findAll());
```

### 3. 自定义 SQL 拦截器

```java
@Component
public class AuditSQLInterceptor implements SQLInterceptor {

    @Override
    public boolean beforeHandler(String sql) {
        // 仅拦截包含敏感表的查询
        return SQLUtils.isQuerySql(sql) && sql.contains("salary");
    }

    @Override
    public DataPermissionSQL postHandler(String sql) throws SQLException {
        RowHandler rowHandler = RowHandlerContext.getInstance().getRowHandler();
        DataPermissionSQLEnhancer enhancer = new DataPermissionSQLEnhancer(sql, rowHandler);
        return new DataPermissionSQL(sql, enhancer.getNewSQL(), enhancer.getTableAlias());
    }

    @Override
    public void afterHandler(String sql, String newSql, SQLException exception) {
        if (exception != null) {
            log.error("SQL 改写失败: {}", sql, exception);
        } else {
            auditLog.record(sql, newSql);
        }
    }
}
```

### 4. 拦截流程说明

```
原始 SQL → SQLRunningContext.intercept()
         → SQLInterceptor.beforeHandler()  // 判断是否需要拦截
         → SQLInterceptor.postHandler()    // JSqlParser 解析 + 注入权限条件
         → SQLInterceptor.afterHandler()   // 日志/审计回调
         → 返回 SQLExecuteState（含改写后的 SQL）
         → JDBC 代理使用改写后的 SQL 执行查询
```

拦截器内部的查询操作会自动设置 `skipInterceptor=true`，防止递归拦截。执行完毕后自动重置状态。
