springboot-starter-data-authorization

数据权限框架

## 框架介绍

基于JDBC的拦截机制实现对数据表列与行的数据查询权限控制。

## 使用教程

1. 配置数据库的JDBC驱动地址为 `com.codingapi.springboot.authorization.jdbc.AuthorizationJdbcDriver`
```
spring.datasource.driver-class-name=com.codingapi.springboot.authorization.jdbc.AuthorizationJdbcDriver
spring.datasource.url=jdbc:h2:file:./test.db
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

```
2. 配置数据权限DataAuthorizationFilter的实现
接口定义
```java

/**
 * 数据权限过滤器
 */
public interface DataAuthorizationFilter {

    /**
     * 列权限过滤
     * @param tableName 表名
     * @param columnName 列名
     * @param value 值
     * @return 过滤后的值
     * @param <T> T
     */
    <T> T columnAuthorization(String tableName, String columnName,T value);

    /**
     * 行权限过滤
     * @param tableName 表名
     * @param tableAlias 表别名
     * @return 过滤后拦截sql条件
     */
    Condition rowAuthorization(String tableName, String tableAlias);

    /**
     * 是否支持列权限过滤
     * @param tableName 表名
     * @param columnName 列名
     * @param value 值
     * @return 是否支持
     */
    boolean supportColumnAuthorization(String tableName, String columnName, Object value);

    /**
     * 是否支持行权限过滤
     * @param tableName 表名
     * @param tableAlias 表别名
     * @return 是否支持
     */
    boolean supportRowAuthorization(String tableName, String tableAlias);
}


```
实例实现：
```

ColumnMaskContext.getInstance().addColumnMask(new IDCardMask());
ColumnMaskContext.getInstance().addColumnMask(new PhoneMask());
ColumnMaskContext.getInstance().addColumnMask(new BankCardMask());

DataAuthorizationContext.getInstance().addDataAuthorizationFilter(new DataAuthorizationFilter() {
    @Override
    public <T> T columnAuthorization(String tableName, String columnName, T value) {
        return ColumnMaskContext.getInstance().mask(value);
    }

    @Override
    public Condition rowAuthorization(String tableName, String tableAlias) {
        if (tableName.equalsIgnoreCase("t_user")) {
            String conditionTemplate = "%s.id > 1 ";
            return Condition.formatCondition(conditionTemplate, tableAlias);
        }
        return null;
    }

    @Override
    public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
        return true;
    }

    @Override
    public boolean supportRowAuthorization(String tableName, String tableAlias) {
        return true;
    }
});
```

实现的拦截器，需要添加到DataAuthorizationContext.getInstance()中才可以使用。可以通过上述实例的手动模式添加，
也可以通过定义DataAuthorizationFilter的@Bean方式添加，当设置为@Bean时既可以自动加入到DataAuthorizationContext.getInstance()中。

## 配置项

在`application.properties`中可用的配置项（配置前缀为`codingapi.data-authorization`）：

```properties
# 是否打印拦截后的SQL，便于调试数据权限条件，默认false
codingapi.data-authorization.show-sql=false
```

| 配置项 | 对应字段 | 默认值 | 说明 |
|--------|----------|--------|------|
| `codingapi.data-authorization.show-sql` | `DataAuthorizationProperties.showSql` | `false` | 设置为`true`时，SQL被拦截改写后会以INFO日志打印拦截后的SQL（`newSql`），便于调试数据权限条件 |

## 扩展点

### 跳过特定SQL的拦截

`DataAuthorizationContext`支持设置跳过拦截时的SQL处理器`SkipAuthorizationFilter`，默认为`DefaultSkipAuthorizationFilter`（原样返回SQL不做任何处理）。
配合`SQLRunningContext.getInstance().skipDataAuthorization()`使用时，被跳过拦截的SQL会先经过`SkipAuthorizationFilter.filter(sql)`处理，
可以通过自定义实现跳过特定SQL的拦截或对SQL进行改写，例如：

```java
DataAuthorizationContext.getInstance().setSkipAuthorizationFilter(sql -> sql);

SQLRunningContext.getInstance().skipDataAuthorization(() -> {
    // 该代码块内的SQL查询不会经过数据权限拦截
    List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
});
```

### 自定义处理器（@Bean自动注册）

在`DataAuthorizationConfiguration`中，通过定义以下类型的`@Bean`即可自动替换框架的默认实现（均以`@Autowired(required = false)`方式注入，未提供时使用默认实现）：

| Bean类型 | 默认实现 | 职责 |
|----------|----------|------|
| `RowHandler` | `DefaultRowHandler` | 行权限处理器，负责为查询SQL构建行级权限过滤条件 |
| `ColumnHandler` | `DefaultColumnHandler` | 列权限处理器，负责对结果集（ResultSet）的列值进行拦截处理 |
| `SQLInterceptor` | `DefaultSQLInterceptor` | SQL拦截器，负责SQL的前置判断（`beforeHandler`）、改写（`postHandler`）与后置处理（`afterHandler`） |

示例：

```java
@Bean
public RowHandler rowHandler() {
    return (subSql, tableName, tableAlias) -> {
        if (tableName.equalsIgnoreCase("t_user")) {
            return Condition.formatCondition("%s.id > 1 ", tableAlias);
        }
        return null;
    };
}
```

