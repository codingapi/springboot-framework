---
name: data-authorization
description: 完整的数据权限框架，包含行级权限（WHERE/JOIN 条件注入）和列级权限（数据脱敏），基于 JSqlParser 的 SQL 增强引擎，提供 ColumnMask SPI、RowHandler/ColumnHandler SPI、SkipAuthorizationFilter 跳过授权机制
status: 已实现
scope: 后端
source: 项目自有
last_commit: 67895fda
code_files:
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/DataAuthorizationContext.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/DataAuthorizationConfiguration.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/enhancer/DataPermissionSQLEnhancer.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/filter/DataAuthorizationFilter.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/filter/DefaultDataAuthorizationFilter.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/filter/SkipAuthorizationFilter.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/handler/RowHandler.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/handler/ColumnHandler.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/handler/ColumnHandlerContext.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/mask/ColumnMask.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/mask/ColumnMaskContext.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/mask/impl/PhoneMask.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/mask/impl/BankCardMask.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/mask/impl/IDCardMask.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/condition/IConditionSQL.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/condition/WhereConditionSQL.java
  - springboot-starter-data-authorization/src/main/java/com/codingapi/springboot/authorization/condition/JoinConditionSQL.java
---

## 解决什么问题

在企业级应用中，不同角色/部门的用户只能看到其权限范围内的数据行，且敏感字段（手机号、银行卡号、身份证号等）需要脱敏展示。如果由每个业务模块自行实现权限过滤和数据脱敏，会导致大量重复代码、规则分散难以维护、容易遗漏造成数据泄露。

本能力提供一套完整的数据权限框架，从两个维度保障数据安全：

- **行级权限**：通过 `RowHandler` SPI 定义过滤规则，`DataPermissionSQLEnhancer` 基于 JSqlParser 解析 SQL AST，自动向 SELECT 语句注入 WHERE 条件或 JOIN 关联表，支持子查询、UNION、嵌套 SELECT 等复杂场景
- **列级权限**：通过 `ColumnHandler` SPI 拦截 ResultSet 读取，结合 `ColumnMask` SPI 对敏感数据自动脱敏。内置手机号、银行卡号、身份证号三种脱敏策略，可按需扩展
- **跳过授权机制**：通过 `SkipAuthorizationFilter` 接口，允许特定 SQL 或场景（如管理后台全量导出）绕过数据权限拦截
- **Spring Boot 自动装配**：通过 `DataAuthorizationConfiguration` 自动注册所有组件，业务方只需实现 SPI 接口并声明为 Bean 即可生效

## 如何使用

### 核心架构

```
DataAuthorizationContext (单例入口)
├── DataAuthorizationFilter[]    — 权限过滤器链（行+列）
├── SkipAuthorizationFilter      — 跳过授权判断
├── RowHandler                   — 行权限条件提供者
├── ColumnHandler                — 列权限结果处理器
└── ColumnMask[]                 — 数据脱敏策略链
```

### 行级权限 — RowHandler

实现 `RowHandler` 接口，根据表名返回需要注入的 SQL 条件：

```java
public interface RowHandler {
    Condition handler(String subSql, String tableName, String tableAlias);
}
```

`Condition` 包含 `List<IConditionSQL>` 条件列表，支持两种条件类型：

- **WhereConditionSQL**：追加 WHERE 条件，如 `"t.dept_id = %d"`
- **JoinConditionSQL**：追加 JOIN 关联表，支持 INNER/LEFT/RIGHT 三种类型，指定表名、别名和 ON 条件

将 `RowHandler` 实现声明为 Spring Bean，`ConditionHandlerRegister` 会自动将其注册到框架中。

### 列级权限 — ColumnHandler

实现 `ColumnHandler` 接口，拦截 ResultSet 中各类型字段的读取：

```java
public interface ColumnHandler {
    String getString(SQLExecuteState state, int columnIndex,
                     String tableName, String columnName, String value);
    int getInt(SQLExecuteState state, int columnIndex,
               String tableName, String columnName, int value);
    // ... 覆盖所有 JDBC ResultSet.getXxx() 方法
}
```

`ColumnHandlerContext` 是单例委托器，框架内部通过它调用 `ColumnHandler`。将 `ColumnHandler` 实现声明为 Spring Bean，`ResultSetHandlerRegister` 会自动注册。

### 数据脱敏 — ColumnMask SPI

```java
public interface ColumnMask {
    boolean support(Object value);  // 判断是否匹配该脱敏规则
    Object mask(Object value);      // 执行脱敏
}
```

内置三种脱敏实现，通过正则匹配自动识别：

| 实现类 | 匹配规则 | 脱敏效果 |
|--------|----------|----------|
| `PhoneMask` | `^1[3-9]\d{9}$` | `138****1234` |
| `BankCardMask` | `^\d{13,19}$` | `622202******1234` |
| `IDCardMask` | 15/18位身份证 | `110101********1234` |

通过 `ColumnMaskContext.getInstance().addColumnMask(mask)` 注册自定义脱敏策略，按注册顺序依次匹配，首个命中即返回。

### 跳过授权 — SkipAuthorizationFilter

```java
public interface SkipAuthorizationFilter {
    String filter(String sql);  // 返回处理后的 SQL，或 null 表示不跳过
}
```

通过 `DataAuthorizationContext.getInstance().setSkipAuthorizationFilter(filter)` 设置。默认使用 `DefaultSkipAuthorizationFilter`（不跳过任何 SQL）。

### 自动配置

`DataAuthorizationConfiguration` 通过 `@Configuration` 自动注册以下 Bean：

- `DataAuthorizationProperties`（配置前缀 `codingapi.data-authorization`）
- `ConditionHandlerRegister`（注入 `RowHandler`）
- `ResultSetHandlerRegister`（注入 `ColumnHandler`）
- `SQLInterceptorRegister`（注入 `SQLInterceptor`）
- `DataAuthorizationContextRegister`（注入所有 `DataAuthorizationFilter` Bean）

所有 Handler/Filter 均为 `@Autowired(required = false)`，未提供时使用默认空实现。

## 使用实例

### 1. 实现行级数据权限

```java
@Component
public class DeptRowHandler implements RowHandler {

    @Override
    public Condition handler(String subSql, String tableName, String tableAlias) {
        if ("employee".equalsIgnoreCase(tableName)) {
            Long deptId = SecurityContext.getCurrentDeptId();
            WhereConditionSQL where = new WhereConditionSQL(
                "%s.dept_id = %d", tableAlias, deptId
            );
            return Condition.of(where);
        }
        return null;
    }
}
```

### 2. 实现列级数据脱敏

```java
@Component
public class SensitiveColumnHandler extends DefaultColumnHandler {

    @Override
    public String getString(SQLExecuteState state, int columnIndex,
                            String tableName, String columnName, String value) {
        // 委托给 ColumnMaskContext 进行自动脱敏
        return ColumnMaskContext.getInstance().mask(value);
    }
}
```

### 3. 注册自定义脱敏策略

```java
@Component
public class EmailMask implements ColumnMask {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w.-]+@[\\w.-]+\\.\\w+$");

    @Override
    public boolean support(Object value) {
        return value instanceof String
            && EMAIL_PATTERN.matcher((String) value).matches();
    }

    @Override
    public Object mask(Object value) {
        String email = (String) value;
        int atIndex = email.indexOf('@');
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}

// 在初始化时注册
@PostConstruct
public void init() {
    ColumnMaskContext.getInstance().addColumnMask(new EmailMask());
}
```

### 4. 使用 JOIN 条件实现跨表行权限

```java
@Component
public class ProjectRowHandler implements RowHandler {

    @Override
    public Condition handler(String subSql, String tableName, String tableAlias) {
        if ("task".equalsIgnoreCase(tableName)) {
            JoinConditionSQL join = new JoinConditionSQL(
                JoinConditionSQL.Type.INNER,
                "project_member",
                "pm",
                String.format("%s.project_id = pm.project_id AND pm.user_id = %d",
                    tableAlias, SecurityContext.getCurrentUserId())
            );
            return Condition.of(join);
        }
        return null;
    }
}
```

### 5. 临时跳过数据权限

```java
// 管理后台导出全量数据
DataAuthorizationContext.getInstance()
    .setSkipAuthorizationFilter(sql -> sql);  // 放行所有 SQL

List<Employee> all = employeeRepository.findAll();

// 恢复正常权限
DataAuthorizationContext.getInstance()
    .setSkipAuthorizationFilter(new DefaultSkipAuthorizationFilter());
```
