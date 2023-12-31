package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import lombok.Getter;

public class MvcRunningContext {

    @Getter
    private final static MvcRunningContext instance = new MvcRunningContext();

    private MvcRunningContext() {
    }

    @Getter
    private DynamicQuery dynamicQuery;
    @Getter
    private JdbcQuery jdbcQuery;


    public void init(DynamicQuery dynamicQuery, JdbcQuery jdbcQuery) {
        this.dynamicQuery = dynamicQuery;
        this.jdbcQuery = jdbcQuery;
    }

}
