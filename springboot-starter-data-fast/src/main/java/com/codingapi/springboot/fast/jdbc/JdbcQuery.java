package com.codingapi.springboot.fast.jdbc;

import org.apache.commons.text.CaseUtils;
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
                map.put(CaseUtils.toCamelCase(columnName,false), rs.getObject(i));
            }
            return map;
        }
    }

    public List<Map<String,Object>> queryForList(String sql, Object... params) {
        return jdbcTemplate.query(sql, params, new CamelCaseRowMapper());
    }

    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) {
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
    }

}
