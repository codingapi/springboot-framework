package com.codingapi.springboot.fast.script;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;


public class ScriptRuntime {

    public static Object running(String script, ScriptContext context) {
        Binding binding = new Binding();
        binding.setVariable("$hql", context.getDynamicQuery());
        binding.setVariable("$jdbc", context.getJdbcQuery());
        GroovyShell groovyShell = new GroovyShell(binding);
        Script userScript = groovyShell.parse(script);
        return userScript.run();
    }
}
