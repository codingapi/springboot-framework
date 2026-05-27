package com.codingapi.springboot.flow.script;

import com.codingapi.springboot.script.GroovyScriptRuntime;
import lombok.Getter;

public class GroovyShellContext {

    // 缓存最大值
    private final static int MAX_CACHE_SIZE = 10000;

    @Getter
    private final static GroovyShellContext instance = new GroovyShellContext();

    private final GroovyScriptRuntime scriptRunner;


    private GroovyShellContext() {
        scriptRunner = new GroovyScriptRuntime(MAX_CACHE_SIZE);
    }


    public <T> T run(String script, Class<T> returnType, Object... args) {
        return scriptRunner.invoke("run", script, returnType, args);
    }


    public int size() {
        return scriptRunner.cacheSize();
    }

    public void clear() {
        scriptRunner.clearCache();
    }

}
