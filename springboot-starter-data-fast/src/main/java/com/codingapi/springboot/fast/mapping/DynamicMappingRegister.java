package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DynamicMappingRegister {

    private final MvcEndpointMapping mvcEndpointMapping;

    private final DynamicQuery dynamicQuery;

    private final JdbcQuery jdbcQuery;

    public DynamicMappingRegister(MvcEndpointMapping mvcEndpointMapping, DynamicQuery dynamicQuery, JdbcTemplate jdbcTemplate) {
        this.mvcEndpointMapping = mvcEndpointMapping;
        this.dynamicQuery = dynamicQuery;
        this.jdbcQuery = new JdbcQuery(jdbcTemplate);
    }

    private void addJdbcMapping(String mapping, RequestMethod requestMethod, String sql, Class<?> clazz, Object ... params)  {
        JdbcMapping handler = new JdbcMapping(jdbcQuery,sql,clazz,params);
        Method method =  handler.getJdbcMethod();
        mvcEndpointMapping.addMapping(mapping, requestMethod, handler, method);
    }


    private void addJdbcMapping(String mapping, RequestMethod requestMethod,String sql, Object ... params)  {
        JdbcMapMapping handler = new JdbcMapMapping(jdbcQuery,sql,params);
        Method method =  handler.getJdbcMapMethod();
        mvcEndpointMapping.addMapping(mapping, requestMethod, handler, method);
    }


    private void addHqlMapping(String mapping, RequestMethod requestMethod,String hql,Class<?> clazz, Object ... params)  {
        HqlMapping handler = new HqlMapping(dynamicQuery,hql,clazz,params);
        Method method =  handler.getHqlMethod();
        mvcEndpointMapping.addMapping(mapping, requestMethod, handler, method);
    }

    /**
     * test dynamic mapping
     * @param dynamicMapping dynamic mapping
     * @return result
     */
    @ResponseBody
    public Object test(DynamicMapping dynamicMapping)  {
        return switch (dynamicMapping.getType()) {
            case JDBC ->
                    new JdbcMapping(jdbcQuery, dynamicMapping.getSql(), dynamicMapping.getClazz(), dynamicMapping.getParams()).execute();
            case HQL ->
                    new HqlMapping(dynamicQuery, dynamicMapping.getSql(), dynamicMapping.getClazz(), dynamicMapping.getParams()).execute();
            case JDBC_MAP ->
                    new JdbcMapMapping(jdbcQuery, dynamicMapping.getSql(), dynamicMapping.getParams()).execute();
        };
    }

    /**
     * add dynamic mapping
     * @param dynamicMapping dynamic mapping
     */
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

    /**
     * remove mapping
     * @param mapping mapping
     * @param requestMethod request method
     */
    public void removeMapping(String mapping,RequestMethod requestMethod)  {
        mvcEndpointMapping.removeMapping(mapping,requestMethod);
    }





    @AllArgsConstructor
    public static class JdbcMapMapping{

        private final JdbcQuery jdbcQuery;
        private final String sql;
        private final Object[] params;

        @ResponseBody
        public List<Map<String,Object>> execute() {
            return jdbcQuery.queryForList(sql, params);
        }

        @SneakyThrows
        private Method getJdbcMapMethod(){
            return this.getClass().getDeclaredMethod("execute");
        }

    }


    @AllArgsConstructor
    public static class JdbcMapping{

        private final JdbcQuery jdbcQuery;
        private final String sql;
        private final Class<?> clazz;
        private final Object[] params;

        @ResponseBody
        public List<?> execute() {
            return jdbcQuery.queryForList(sql, clazz, params);
        }

        @SneakyThrows
        private Method getJdbcMethod(){
            return this.getClass().getDeclaredMethod("execute");
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

        @SneakyThrows
        private Method getHqlMethod(){
            return this.getClass().getDeclaredMethod("execute");
        }
    }

}
