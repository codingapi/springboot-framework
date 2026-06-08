---
name: data-authorization
description: 基于 JDBC 代理链的透明数据权限过滤，支持行级和列级权限控制
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在企业级多租户或多部门系统中，不同用户只能查看和操作其权限范围内的数据。传统做法需要在每个业务查询中手动拼接权限条件，代码侵入性强且容易遗漏。

`springboot-starter-data-authorization` 通过 JDBC 驱动层代理拦截所有 SQL 执行，在 SQL 到达数据库之前自动注入行级过滤条件（WHERE / JOIN），并在结果集返回时提供列级权限处理能力。业务代码无需任何修改即可获得透明的数据权限控制。

核心设计思路：
- **JDBC 驱动代理**：`AuthorizationJdbcDriver` 作为 JDBC Driver 注册，将所有 `Connection` 包装为 `ConnectionProxy`
- **SQL 拦截改写**：通过 `SQLRunningContext.intercept(sql)` 在 `prepareStatement` / `prepareCall` / `createStatement` 时解析并改写 SQL
- **行级权限**：`RowHandler` + `DataAuthorizationFilter` 为每张表动态注入 WHERE 条件或 JOIN 关联
- **列级权限**：`ColumnHandler` 在 `ResultSetProxy` 读取字段值时进行脱敏或过滤
- **跳过机制**：`SQLRunningContext.skipDataAuthorization()` 允许特定操作绕过权限拦截

## 如何使用

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-data-authorization</artifactId>
</dependency>
```

### 2. 配置 JDBC 驱动

将 JDBC URL 中的 driver 替换为 `com.codingapi.springboot.authorization.jdbc.AuthorizationJdbcDriver`，该驱动会自动查找并代理真实的数据库驱动：

```properties
spring.datasource.driver-class-name=com.codingapi.springboot.authorization.jdbc.AuthorizationJdbcDriver
```

### 3. 注册数据权限过滤器

实现 `DataAuthorizationFilter` 接口并注册到 `DataAuthorizationContext`：

```java
// 添加行级权限过滤器
DataAuthorizationContext.getInstance().addDataAuthorizationFilter(filter);
```

`DataAuthorizationFilter` 需要实现以下方法：
- `supportRowAuthorization(tableName, tableAlias)` — 判断是否对指定表启用行级权限
- `rowAuthorization(tableName, tableAlias)` — 返回 `Condition` 对象，包含要注入的 WHERE 或 JOIN 条件
- `supportColumnAuthorization(tableName, columnName, value)` — 判断是否对指定列启用列级权限
- `columnAuthorization(tableName, columnName, value)` — 返回处理后的列值

### 4. 构建行级权限条件

使用 `Condition` 类的静态工厂方法构建权限条件：

```java
// WHERE 条件
Condition.customCondition("department_id = 10");

// 格式化条件
Condition.formatCondition("department_id = %d", deptId);

// 默认放行条件
Condition.defaultCondition(); // 等价于 "1=1"

// 无权限（返回 null）
Condition.emptyCondition();
```

对于 JOIN 类型的权限条件，框架内置了 `JoinConditionSQLHandler`，支持 INNER / LEFT / RIGHT JOIN 的自动注入。

### 5. 自定义 RowHandler

如需自定义行权限处理逻辑，可实现 `RowHandler` 接口并通过 `RowHandlerContext` 设置：

```java
RowHandlerContext.getInstance().setRowHandler((subSql, tableName, tableAlias) -> {
    return Condition.customCondition("tenant_id = '" + getCurrentTenantId() + "'");
});
```

### 6. 自定义列级权限处理器

实现 `ColumnHandler` 接口以对特定列的值进行脱敏或转换：

```java
// ColumnHandler 接口提供了所有 JDBC 类型的 get 方法
String getString(SQLExecuteState interceptState, int columnIndex, 
                 String tableName, String columnName, String value);
int getInt(SQLExecuteState interceptState, int columnIndex, 
           String tableName, String columnName, int value);
// ... 其他类型同理
```

### 7. 跳过数据权限拦截

在某些管理操作或系统内部查询中需要绕过权限过滤：

```java
// 方式一：使用 Supplier
List<User> users = SQLRunningContext.getInstance()
    .skipDataAuthorization(() -> userRepository.findAll());

// 方式二：使用 Runnable
SQLRunningContext.getInstance()
    .skipDataAuthorization(() -> userRepository.deleteAll());
```

### 8. 自定义跳过拦截时的 SQL 处理

实现 `SkipAuthorizationFilter` 接口，在跳过权限拦截时对 SQL 进行额外处理：

```java
DataAuthorizationContext.getInstance()
    .setSkipAuthorizationFilter(sql -> {
        // 对跳过拦截的 SQL 做特殊处理
        return sql;
    });
```

## 使用实例

### 完整的多租户数据权限示例

```java
@Configuration
public class DataPermissionConfig {

    @PostConstruct
    public void initDataPermission() {
        // 注册数据权限过滤器
        DataAuthorizationContext.getInstance().addDataAuthorizationFilter(
            new DataAuthorizationFilter() {

                @Override
                public boolean supportRowAuthorization(String tableName, String tableAlias) {
                    // 对订单表和员工表启用行级权限
                    return "t_order".equals(tableName) || "t_employee".equals(tableName);
                }

                @Override
                public Condition rowAuthorization(String tableName, String tableAlias) {
                    Long deptId = SecurityUtils.getCurrentDeptId();
                    if (deptId == null) {
                        return Condition.emptyCondition();
                    }
                    return Condition.formatCondition("%s.dept_id = %d", tableAlias, deptId);
                }

                @Override
                public <T> boolean supportColumnAuthorization(
                        String tableName, String columnName, T value) {
                    // 对员工表的手机号列启用脱敏
                    return "t_employee".equals(tableName) && "phone".equals(columnName);
                }

                @Override
                public <T> T columnAuthorization(
                        String tableName, String columnName, T value) {
                    if (value instanceof String phone) {
                        return (T) phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                    }
                    return value;
                }
            }
        );
    }
}
```

### 代理链工作原理

```
应用代码调用 prepareStatement(sql)
    ↓
ConnectionProxy.prepareStatement(sql)
    ↓
SQLRunningContext.intercept(sql)
    ├── skipInterceptor=true → 直接返回原始 SQL
    └── SQLInterceptor.beforeHandler(sql) → true
         ↓
        SQLInterceptor.postHandler(sql) → DataPermissionSQL(newSql, aliasContext)
         ↓
        connection.prepareStatement(newSql) → PreparedStatementProxy
         ↓
        executeQuery() → ResultSetProxy(resultSet, interceptState)
         ↓
        ResultSetProxy.next()/getXxx() → ColumnHandler 列级权限处理
```

### 异常处理

当权限校验失败时，框架抛出 `NotAuthorizationException`（继承自 `SQLException`），可在上层统一捕获处理：

```java
try {
    List<Order> orders = orderRepository.findAll();
} catch (NotAuthorizationException e) {
    // 处理无权限异常
    throw new BusinessException("无权访问该数据");
}
```
