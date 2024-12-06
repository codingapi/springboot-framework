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

