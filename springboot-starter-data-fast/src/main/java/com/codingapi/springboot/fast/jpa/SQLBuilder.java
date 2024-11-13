package com.codingapi.springboot.fast.jpa;

import java.util.ArrayList;
import java.util.List;

public class SQLBuilder {

    private final StringBuilder sqlBuilder;
    private final StringBuilder countSQLBuilder;
    private int index;
    private final List<Object> params;


    public SQLBuilder(String sql) {
        this(sql,"select count(1) from "+sql);
    }

    public SQLBuilder(String sql, String countSQL) {
        this.countSQLBuilder = new StringBuilder(countSQL);
        this.sqlBuilder = new StringBuilder(sql);
        this.index = 1;
        this.params = new ArrayList<>();
    }

    public void append(String sql,Object value) {
        if (value != null) {
            sqlBuilder.append(" ").append(sql).append(index).append(" ");
            countSQLBuilder.append(" ").append(sql).append(index).append(" ");
            params.add(value);
            index++;
        }
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
