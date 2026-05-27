package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQueryContext;
import com.codingapi.springboot.fast.jpa.JpaQuery;
import com.codingapi.springboot.fast.jpa.JpaQueryContext;
import com.codingapi.springboot.script.GroovyScriptRunner;
import com.codingapi.springboot.script.request.GroovyBindObjectBuilder;
import lombok.Getter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class ScriptRuntime {


    @Getter
    private final static ScriptRuntime instance = new ScriptRuntime();

    private final GroovyScriptRunner scriptRunner;

    private ScriptRuntime(){
        this.scriptRunner = new GroovyScriptRunner(1000);
    }

    Object running(String script) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        ScriptRequest request = new ScriptRequest(attributes.getRequest());
        JdbcQuery jdbcQuery = JdbcQueryContext.getInstance().getJdbcQuery();
        JpaQuery jpaQuery = JpaQueryContext.getInstance().getJpaQuery();

        GroovyBindObjectBuilder bindBuilder = GroovyBindObjectBuilder.builder();
        bindBuilder.add("$request", request);
        bindBuilder.add("$jpa", jpaQuery);
        bindBuilder.add("$jdbc", jdbcQuery);

        return this.scriptRunner.run(script,Object.class,bindBuilder.build());
    }
}
