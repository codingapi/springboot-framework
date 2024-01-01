package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Getter
@Setter
public class SQLMapping extends BaseMapping{

    private String sql;
    private Class<?> clazz;
    private Object[] params;
    private Type type;

    public SQLMapping(Type type,String mapping, RequestMethod requestMethod, String sql, Class<?> clazz, Object[] params) {
        super(mapping, requestMethod);
        this.sql = sql;
        this.clazz = clazz;
        this.params = params;
        this.type = type;
    }

    public static SQLMapping jdbcMapping(String mapping, RequestMethod requestMethod, String sql, Class<?> clazz, Object... params) {
        return new SQLMapping(Type.JDBC, mapping, requestMethod, sql, clazz, params);
    }

    public static SQLMapping jdbcMapMapping(String mapping, RequestMethod requestMethod, String sql, Object... params) {
        return new SQLMapping(Type.JDBC_MAP, mapping, requestMethod, sql, null, params);
    }

    public static SQLMapping hqlMapping(String mapping, RequestMethod requestMethod, String sql, Class<?> clazz, Object... params) {
        return new SQLMapping(Type.HQL, mapping, requestMethod, sql, clazz, params);
    }


    enum Type {
        JDBC, HQL, JDBC_MAP
    }


    @ResponseBody
    public SingleResponse<Object> execute() {
        return switch (type) {
            case JDBC -> SingleResponse.of(MvcRunningContext.getInstance().getJdbcQuery().queryForList(sql, clazz, params));
            case HQL -> SingleResponse.of(MvcRunningContext.getInstance().getDynamicQuery().listQuery(clazz, sql, params));
            case JDBC_MAP -> SingleResponse.of(MvcRunningContext.getInstance().getJdbcQuery().queryForList(sql, params));
        };
    }

}
