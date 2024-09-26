package com.codingapi.springboot.flow.script;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.function.Consumer;

public class ScriptRuntime {

    public static <T> T run(String script,
                            Consumer<Binding> consumer,
                            Class<T> clazz) {
        Binding binding = new Binding();
        consumer.accept(binding);
        GroovyShell groovyShell = new GroovyShell(binding);
        Script userScript = groovyShell.parse(script);
        return (T) userScript.run();
    }
}
