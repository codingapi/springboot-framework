---
name: springboot-starter-data-authorization/jdbc-proxy
module: springboot-starter-data-authorization
description: 自定义 JDBC 驱动代理（Connection/Statement/PreparedStatement/CallableStatement/ResultSet），在结果集读取时按列权限脱敏
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-data-authorization"
symbols:
  - ConnectionProxy
  - StatementProxy
  - PreparedStatementProxy
  - CallableStatementProxy
  - ResultSetProxy
content_hash: 6008a8c44ddcc660b4d1d802a6357cf4fa41ae7da5b798f80e5b995187192397
---

## 解决什么问题

数据权限的最后一公里问题：业务 SQL 已经按 `DefaultSQLInterceptor` 增强了 WHERE 条件，但 **列级权限**（如手机号脱敏、身份证屏蔽）需要在结果集返回前对每一列做后置处理。

JDBC 规范要求返回的 `ResultSet` 是只读视图，业务方代码无法在数据离开驱动层后再修改。本能力通过自定义 JDBC 驱动 + 全套 Proxy 类，在 `getString/getInt/getBigDecimal` 等方法被调用时统一走 `ColumnHandlerContext` 进行列权限判断与脱敏。

## 如何使用

**1. 启用框架自动装配**

引入 `springboot-starter-data-authorization` 依赖后，框架会注册 `AuthorizationJdbcDriver`。业务代码通过 `DriverManager.getConnection(url, props)` 时，传入自定义 URL 前缀即可走代理。

**2. 注册列处理器（按表/列粒度）**

```java
@Component
public class PhoneColumnHandler extends ColumnHandler {
    @Override
    public String getString(SQLExecuteState state, int columnIndex, String tableName,
                            String columnName, String value) {
        if ("user".equalsIgnoreCase(tableName) && "phone".equalsIgnoreCase(columnName)
            && !hasPermission(state, "user:phone:read")) {
            return maskPhone(value);  // 138****1234
        }
        return value;
    }
}
```

**3. 注册 RowHandler 控制行级权限**

行级权限由 `DefaultSQLInterceptor` 配合 `RowHandler` 完成 SQL 改写；列级权限则由 `ColumnHandler` 在结果集读取时完成（详见 `ResultSetProxy.getString(int)` 等方法）。

**4. 支持的列类型**

`ResultSetProxy` 完整代理了以下方法的重载：
- `getString` / `getBoolean` / `getByte` / `getShort` / `getInt` / `getLong`
- `getFloat` / `getDouble` / `getBigDecimal` / `getBytes`
- `getDate` / `getTime` / `getTimestamp`
- `getAsciiStream` / `getUnicodeStream` / `getBinaryStream` / `getObject`

每种类型都通过 `ColumnHandlerContext` 的对应方法转发。

## 使用实例

```java
// 1. 业务代码无需感知权限：直接通过标准 JDBC 读取
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement("SELECT id, name, phone FROM user");
     ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {
        String name = rs.getString("name");
        String phone = rs.getString("phone");  // 自动脱敏：138****1234
    }
}

// 2. 自定义列权限策略
@Component
public class SalaryColumnHandler extends ColumnHandler {
    @Override
    public String getString(SQLExecuteState state, int columnIndex, String tableName,
                            String columnName, String value) {
        if ("employee".equalsIgnoreCase(tableName) && "salary".equalsIgnoreCase(columnName)
            && !isHR(state.getUserId())) {
            return "***";  // 非 HR 不可见
        }
        return value;
    }
}

// 3. 与 SQL 拦截器配合：
// SQL 拦截器在 SQL 执行前改写 WHERE 条件（行级权限）
// JDBC 代理在结果集返回时改写列值（列级权限）
// 二者结合形成完整的"行+列"数据权限
```
