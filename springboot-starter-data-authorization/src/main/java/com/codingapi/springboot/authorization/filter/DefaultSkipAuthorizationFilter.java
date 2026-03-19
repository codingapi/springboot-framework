package com.codingapi.springboot.authorization.filter;

public class DefaultSkipAuthorizationFilter implements SkipAuthorizationFilter{

    @Override
    public String filter(String sql) {
        return sql;
    }
}
