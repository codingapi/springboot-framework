package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.jdbc.JdbcQuery;
import com.codingapi.springboot.fast.jdbc.JdbcQueryContext;
import com.codingapi.springboot.fast.jpa.JpaQuery;
import com.codingapi.springboot.fast.jpa.JpaQueryContext;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class ScriptRuntime {

    static Object running(String script) {
        Binding binding = new Binding();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        ScriptRequest request = new ScriptRequest(attributes.getRequest());
        JdbcQuery jdbcQuery = JdbcQueryContext.getInstance().getJdbcQuery();
        JpaQuery jpaQuery = JpaQueryContext.getInstance().getJpaQuery();

        binding.setVariable("$request", request);
        binding.setVariable("$jpa", jpaQuery);
        binding.setVariable("$jdbc", jdbcQuery);

        GroovyShell groovyShell = new GroovyShell(binding);
        Script userScript = groovyShell.parse(script);
        return userScript.run();
    }
}
