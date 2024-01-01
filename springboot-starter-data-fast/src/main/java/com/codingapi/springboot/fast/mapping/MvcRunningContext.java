package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.jpa.JPAQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import lombok.Getter;

public class MvcRunningContext {

    @Getter
    private final static MvcRunningContext instance = new MvcRunningContext();

    private MvcRunningContext() {
    }

    @Getter
    private JPAQuery JPAQuery;
    @Getter
    private JdbcQuery jdbcQuery;


    public void init(JPAQuery JPAQuery, JdbcQuery jdbcQuery) {
        this.JPAQuery = JPAQuery;
        this.jdbcQuery = jdbcQuery;
    }

}
