---
name: commons-text
description: 文本处理工具库，提供字符串命名转换等功能
status: 已实现
scope: 后端
source: 框架:Apache Commons Text
---

## 解决什么问题

在数据查询模块（springboot-starter-data-fast）中，JDBC 查询返回的结果集列名通常是数据库风格的下划线命名（如 `user_name`），而 Java Bean 属性使用驼峰命名（如 `userName`）。需要自动将列名转换为驼峰格式以匹配 Bean 属性。

Apache Commons Text 的 `CaseUtils.toCamelCase()` 提供了开箱即用的命名风格转换功能。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明版本 1.12.0 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.12.0</version>
</dependency>
```

### 核心用法

主要使用 `org.apache.commons.text.CaseUtils` 进行命名风格转换。

## 使用实例

### JdbcQuery 中的驼峰命名映射

```java
// JdbcQuery.java - JDBC 查询结果集的列名转驼峰
import org.apache.commons.text.CaseUtils;

public class JdbcQuery {

    /**
     * 自定义 RowMapper，将下划线列名自动转为驼峰命名
     */
    private static class CamelCaseRowMapper implements RowMapper<Map<String, Object>> {

        @Override
        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> map = new HashMap<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                // 将 user_name → userName, create_time → createTime
                map.put(CaseUtils.toCamelCase(columnName, false), rs.getObject(i));
            }
            return map;
        }
    }

    /**
     * 查询返回 Map 列表（自动驼峰转换）
     */
    public List<Map<String, Object>> queryForMapList(String sql, Object... params) {
        return jdbcTemplate.query(sql, new CamelCaseRowMapper(), params);
    }

    /**
     * 分页查询返回 Map 页面（自动驼峰转换）
     */
    public Page<Map<String, Object>> queryForMapPage(String sql, String countSql,
                                                      PageRequest pageRequest,
                                                      Object... params) {
        List<Map<String, Object>> list = jdbcTemplate.query(
            sql, new CamelCaseRowMapper(), params
        );
        long count = this.countQuery(countSql, params);
        return new PageImpl<>(list, pageRequest, count);
    }
}
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter-data-fast | `JdbcQuery.CamelCaseRowMapper` 列名下划线转驼峰 |
