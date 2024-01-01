package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.mapping.MvcRunningContext;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class ScriptRuntime {

    public static Object running(String script, MvcRunningContext context) {
        Binding binding = new Binding();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        ScriptRequest request =  new ScriptRequest(attributes.getRequest());
        binding.setVariable("$request", request);
        binding.setVariable("$jpa", context.getJPAQuery());
        binding.setVariable("$jdbc", context.getJdbcQuery());
        GroovyShell groovyShell = new GroovyShell(binding);
        Script userScript = groovyShell.parse(script);
        return userScript.run();
    }
}
