package com.codingapi.springboot.fast.jdbc;

import com.codingapi.springboot.fast.jpa.SQLBuilder;
import org.apache.commons.text.CaseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcQuery {

    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public JdbcQuery(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class CamelCaseRowMapper implements RowMapper<Map<String, Object>> {

        @Override
        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> map = new HashMap<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                // 修复：必须显式指定下划线分隔符，否则 CaseUtils 不做任何驼峰转换
                map.put(CaseUtils.toCamelCase(columnName, false, '_'), rs.getObject(i));
            }
            return map;
        }
    }

    public List<Map<String, Object>> queryForMapList(SQLBuilder<?> builder) {
        return queryForMapList(builder.getSQL(), builder.getParams());
    }

    public List<Map<String, Object>> queryForMapList(String sql, Object... params) {
        return jdbcTemplate.query(sql, new CamelCaseRowMapper(), params);
    }

    public <T> List<T> queryForList(SQLBuilder<T> builder) {
        return queryForList(builder.getSQL(), builder.getClazz(), builder.getParams());
    }

    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) {
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(clazz), params);
    }

    public <T> Page<T> queryForPage(SQLBuilder<T> builder, PageRequest pageRequest) {
        return queryForPage(builder.getSQL(), builder.getCountSQL(), builder.getClazz(), pageRequest, builder.getParams());
    }

    public <T> Page<T> queryForPage(String sql, String countSql, Class<T> clazz, PageRequest pageRequest, Object... params) {
        List<T> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(clazz), params);
        long count = this.countQuery(countSql, params);
        return new PageImpl<>(list, pageRequest, count);
    }

    public <T> Page<T> queryForPage(String sql, Class<T> clazz, PageRequest pageRequest, Object... params) {
        String countSql = "SELECT COUNT(1) " + sql;
        return this.queryForPage(sql, countSql, clazz, pageRequest, params);
    }

    public Page<Map<String, Object>> queryForMapPage(SQLBuilder<?> builder, PageRequest pageRequest) {
        return queryForMapPage(builder.getSQL(), builder.getCountSQL(), pageRequest, builder.getParams());
    }

    public Page<Map<String, Object>> queryForMapPage(String sql, String countSql, PageRequest pageRequest, Object... params) {
        List<Map<String, Object>> list = jdbcTemplate.query(sql, new CamelCaseRowMapper(), params);
        long count = this.countQuery(countSql, params);
        return new PageImpl<>(list, pageRequest, count);
    }

    public Page<Map<String, Object>> queryForMapPage(String sql, PageRequest pageRequest, Object... params) {
        String countSql = "SELECT COUNT(1) " + sql;
        return this.queryForMapPage(sql, countSql, pageRequest, params);
    }


    private long countQuery(String sql, Object... params) {
        int countSqlParamsLength = sql.split("\\?", -1).length - 1;
        Long count;
        if (countSqlParamsLength <= 0) {
            count = jdbcTemplate.queryForObject(sql, Long.class);
        } else {
            Object[] newParams = new Object[countSqlParamsLength];
            // 修复：原实现仅在 params 数量大于占位符数量时才拷贝，数量相等时参数全部丢失导致 SQL 参数未绑定
            System.arraycopy(params, 0, newParams, 0, Math.min(params.length, countSqlParamsLength));
            count = jdbcTemplate.queryForObject(sql, Long.class, newParams);
        }
        return count == null ? 0L : count;
    }
}
