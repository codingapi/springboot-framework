package com.codingapi.springboot.permission.sql;

public class JdbcValue {

    private final Object value;

    public JdbcValue(Object value) {
        this.value = value;
    }

    public String strVal() {
        if(value==null) {
            return "NULL";
        }
        return String.format("'%s'",value);
    }
}
