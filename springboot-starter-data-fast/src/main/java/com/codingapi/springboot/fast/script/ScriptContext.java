package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import lombok.Getter;

public class ScriptContext {

    @Getter
    private final static ScriptContext instance = new ScriptContext();


    private ScriptContext() {

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
