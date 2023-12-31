package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.mapping.MvcRunningContext;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;


public class ScriptRuntime {

    public static Object running(String script, MvcRunningContext context) {
        Binding binding = new Binding();
        binding.setVariable("$jpa", context.getDynamicQuery());
        binding.setVariable("$jdbc", context.getJdbcQuery());
        GroovyShell groovyShell = new GroovyShell(binding);
        Script userScript = groovyShell.parse(script);
        return userScript.run();
    }
}
