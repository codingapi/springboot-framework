package com.codingapi.springboot.fast.jpa;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SQLBuilder {

    private final StringBuilder sqlBuilder;
    private final StringBuilder countSQLBuilder;
    @Getter
    private int index;
    private final List<Object> params;
    @Getter
    private final Class<?> clazz;

    public SQLBuilder(String sql) {
        this(null, sql, "select count(1) from " + sql);
    }

    public SQLBuilder(Class<?> clazz, String sql) {
        this(clazz, sql, "select count(1) from " + sql);
    }

    public SQLBuilder(String sql,String countSql) {
        this(null, sql, countSql);
    }

    public SQLBuilder(Class<?> clazz, String sql, String countSQL) {
        this.countSQLBuilder = new StringBuilder(countSQL);
        this.sqlBuilder = new StringBuilder(sql);
        this.index = 1;
        this.params = new ArrayList<>();
        this.clazz = clazz;
    }

    public void append(String sql, Object value) {
        if (value != null) {
            sqlBuilder.append(" ").append(sql).append(index).append(" ");
            countSQLBuilder.append(" ").append(sql).append(index).append(" ");
            params.add(value);
            index++;
        }
    }

    public void addParam(Object value){
        params.add(value);
        index++;
    }

    public void addParam(Object value,int index){
        params.add(value);
        this.index = index;
    }

    public void appendSql(String sql){
        sqlBuilder.append(" ").append(sql).append(" ");
        countSQLBuilder.append(" ").append(sql).append(" ");
    }

    public String getSQL() {
        return sqlBuilder.toString();
    }

    public String getCountSQL() {
        return countSQLBuilder.toString();
    }

    public Object[] getParams() {
        return params.toArray();
    }


}
