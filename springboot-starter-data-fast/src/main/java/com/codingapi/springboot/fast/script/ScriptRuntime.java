package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQueryContext;
import com.codingapi.springboot.fast.jpa.JpaQuery;
import com.codingapi.springboot.fast.jpa.JpaQueryContext;
import com.codingapi.springboot.script.GroovyScriptRuntime;
import lombok.Getter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;


public class ScriptRuntime {


    @Getter
    private final static ScriptRuntime instance = new ScriptRuntime();

    private final GroovyScriptRuntime scriptRunner;

    private ScriptRuntime(){
        this.scriptRunner = new GroovyScriptRuntime(1000);
    }

    Object running(String script) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        ScriptRequest request = new ScriptRequest(attributes.getRequest());
        JdbcQuery jdbcQuery = JdbcQueryContext.getInstance().getJdbcQuery();
        JpaQuery jpaQuery = JpaQueryContext.getInstance().getJpaQuery();

        Map<String,Object> bindBuilder = new HashMap<>();
        bindBuilder.put("$request", request);
        bindBuilder.put("$jpa", jpaQuery);
        bindBuilder.put("$jdbc", jdbcQuery);

        return this.scriptRunner.run(script,Object.class,bindBuilder);

    }
}
