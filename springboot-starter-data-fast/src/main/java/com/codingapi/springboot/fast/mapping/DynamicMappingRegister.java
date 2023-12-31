package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.text.CaseUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DynamicMappingRegister {

    private final MvcEndpointMapping mvcEndpointMapping;

    private final DynamicQuery dynamicQuery;

    private final JdbcTemplate jdbcTemplate;


    private void addJdbcMapping(String mapping, RequestMethod requestMethod,String sql,Class<?> clazz, Object ... params)  {
        Object handler = new JdbcMapping( jdbcTemplate,sql,clazz,params);
        Method method =  getJdbcMethod();
        mvcEndpointMapping.addMapping(mapping, requestMethod, handler, method);
    }


    private void addJdbcMapping(String mapping, RequestMethod requestMethod,String sql, Object ... params)  {
        Object handler = new JdbcMapMapping( jdbcTemplate,sql,params);
        Method method =  getJdbcMapMethod();
        mvcEndpointMapping.addMapping(mapping, requestMethod, handler, method);
    }


    private void addHqlMapping(String mapping, RequestMethod requestMethod,String hql,Class<?> clazz, Object ... params)  {
        Object handler = new HqlMapping(dynamicQuery,hql,clazz,params);
        Method method =  getHqlMethod();
        mvcEndpointMapping.addMapping(mapping, requestMethod, handler, method);
    }

    public void addMapping(DynamicMapping dynamicMapping)  {
        switch (dynamicMapping.getType()) {
            case JDBC:
                addJdbcMapping(dynamicMapping.getMapping(), dynamicMapping.getRequestMethod(), dynamicMapping.getSql(), dynamicMapping.getClazz(), dynamicMapping.getParams());
                break;
            case HQL:
                addHqlMapping(dynamicMapping.getMapping(), dynamicMapping.getRequestMethod(), dynamicMapping.getSql(), dynamicMapping.getClazz(), dynamicMapping.getParams());
                break;
            case JDBC_MAP:
                addJdbcMapping(dynamicMapping.getMapping(), dynamicMapping.getRequestMethod(), dynamicMapping.getSql(), dynamicMapping.getParams());
                break;
            default:
                throw new RuntimeException("not support type");
        }
    }



    @SneakyThrows
    private Method getJdbcMethod(){
        return JdbcMapping.class.getDeclaredMethod("execute");
    }

    @SneakyThrows
    private Method getJdbcMapMethod(){
        return JdbcMapMapping.class.getDeclaredMethod("execute");
    }

    @SneakyThrows
    private Method getHqlMethod(){
        return HqlMapping.class.getDeclaredMethod("execute");
    }


    @AllArgsConstructor
    public static class JdbcMapMapping{

        private final JdbcTemplate jdbcTemplate;
        private final String sql;
        private final Object[] params;

        @ResponseBody
        public List<Map<String,Object>> execute() {
            return jdbcTemplate.query(sql, params, new CamelCaseRowMapper());
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
    }


    @AllArgsConstructor
    public static class JdbcMapping{

        private final JdbcTemplate jdbcTemplate;
        private final String sql;
        private final Class<?> clazz;
        private final Object[] params;

        @ResponseBody
        public List<?> execute() {
            return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
        }



    }


    @AllArgsConstructor
    public static class HqlMapping{

        private final DynamicQuery dynamicQuery;
        private final String hql;
        private final Class<?> clazz;
        private final Object[] params;

        @ResponseBody
        public List<?> execute() {
            return dynamicQuery.listQuery(clazz,hql, params);
        }
    }

}
