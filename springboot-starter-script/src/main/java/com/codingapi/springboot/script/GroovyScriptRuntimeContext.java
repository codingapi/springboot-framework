package com.codingapi.springboot.script;

import lombok.Getter;

import java.util.Map;

public class GroovyScriptRuntimeContext {

    @Getter
    private final static GroovyScriptRuntimeContext instance = new GroovyScriptRuntimeContext();

    public static final int DEFAULT_MAX_CACHE_SIZE = 10 * 1024;

    private final GroovyScriptRuntime scriptRuntime;

    private GroovyScriptRuntimeContext() {
        int maxCacheSize = Integer.parseInt(System.getProperty("DEFAULT_MAX_CACHE_SIZE", String.valueOf(DEFAULT_MAX_CACHE_SIZE)));
        System.out.println("GroovyScriptRuntimeContext max cache size:" + maxCacheSize);
        this.scriptRuntime = new GroovyScriptRuntime(maxCacheSize);
    }

    public int getMaxCacheSize() {
        return scriptRuntime.getMaxCacheSize();
    }

    public void clearCache() {
        scriptRuntime.clearCache();
    }

    public int cacheSize() {
        return scriptRuntime.cacheSize();
    }

    public void compile(String script, boolean cache) {
        scriptRuntime.compile(script, cache);
    }

    public void compile(String script) {
        scriptRuntime.compile(script);
    }

    public <T> T invoke(String method, String script, Class<T> returnType, TransactionMode transactionMode, Map<String,Object> binds, Object... requests) {
        return scriptRuntime.invoke(method, script, returnType, transactionMode, binds, requests);
    }


    public <T> T run(String script, Class<T> returnType, TransactionMode transactionMode, Map<String,Object> binds) {
        return scriptRuntime.run(script, returnType, transactionMode, binds);
    }
}
