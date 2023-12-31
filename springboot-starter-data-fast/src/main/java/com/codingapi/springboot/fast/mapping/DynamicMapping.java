package com.codingapi.springboot.fast.mapping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

@Getter
@Setter
@NoArgsConstructor
public class DynamicMapping {

    private String mapping;
    private RequestMethod requestMethod;
    private String sql;
    private Class<?> clazz;
    private Object[] params;
    private Type type;

    public DynamicMapping(Type type, String mapping, RequestMethod requestMethod, String sql, Class<?> clazz, Object... params) {
        this.type = type;
        this.mapping = mapping;
        this.requestMethod = requestMethod;
        this.sql = sql;
        this.clazz = clazz;
        this.params = params;
    }


    public static DynamicMapping jdbcMapping(String mapping, RequestMethod requestMethod, String sql, Class<?> clazz, Object... params) {
        return new DynamicMapping(Type.JDBC, mapping, requestMethod, sql, clazz, params);
    }

    public static DynamicMapping jdbcMapMapping(String mapping, RequestMethod requestMethod, String sql, Object... params) {
        return new DynamicMapping(Type.JDBC_MAP, mapping, requestMethod, sql, null, params);
    }

    public static DynamicMapping hqlMapping(String mapping, RequestMethod requestMethod, String sql, Class<?> clazz, Object... params) {
        return new DynamicMapping(Type.HQL, mapping, requestMethod, sql, clazz, params);
    }



    enum Type {
        JDBC, HQL, JDBC_MAP
    }

}
