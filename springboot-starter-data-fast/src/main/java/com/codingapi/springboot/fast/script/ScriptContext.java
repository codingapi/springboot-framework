package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;

public class ScriptContext {

    @Getter
    private final static ScriptContext instance = new ScriptContext();


    private ScriptContext() {

    }


    @Getter
    private DynamicQuery dynamicQuery;
    @Getter
    private JdbcTemplate jdbcTemplate;


    public void init(DynamicQuery dynamicQuery, JdbcTemplate jdbcTemplate) {
        this.dynamicQuery = dynamicQuery;
        this.jdbcTemplate = jdbcTemplate;
    }

}
