package com.codingapi.springboot.flow.script;

import com.codingapi.springboot.framework.script.GroovyScriptRunner;
import lombok.Getter;

public class GroovyShellContext {

    // 缓存最大值
    private final static int MAX_CACHE_SIZE = 10000;

    @Getter
    private final static GroovyShellContext instance = new GroovyShellContext();

    private final GroovyScriptRunner scriptRunner;


    private GroovyShellContext() {
        scriptRunner = new GroovyScriptRunner(MAX_CACHE_SIZE);
    }


    public <T> T run(String script, Class<T> returnType, Object... args) {
        return scriptRunner.run("run", script, returnType, args);
    }


    public int size() {
        return scriptRunner.cacheSize();
    }

    public void clear() {
        scriptRunner.clearCache();
    }

}
