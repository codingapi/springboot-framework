package com.codingapi.springboot.flow.script;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class ScriptRuntime {

    public static <T> T run(String script,
                            Consumer<Binding> consumer,
                            Class<T> clazz) {
        try {
            Binding binding = new Binding();
            consumer.accept(binding);
            GroovyShell groovyShell = new GroovyShell(binding);
            Script userScript = groovyShell.parse(script);
            return (T) userScript.run();
        }catch (Throwable e){
            log.error("script error:{}",script);
            throw e;
        }
    }
}
