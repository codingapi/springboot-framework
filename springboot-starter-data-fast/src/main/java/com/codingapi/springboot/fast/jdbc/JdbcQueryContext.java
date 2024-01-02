package com.codingapi.springboot.fast.jdbc;


import lombok.Getter;

public class JdbcQueryContext {

    @Getter
    private static final JdbcQueryContext instance = new JdbcQueryContext();

    private JdbcQueryContext() {

    }

    @Getter
    private JdbcQuery jdbcQuery;

    void setJdbcQuery(JdbcQuery jdbcQuery) {
        this.jdbcQuery = jdbcQuery;
    }


}
