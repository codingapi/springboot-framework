package com.codingapi.springboot.flow.script;

import com.codingapi.springboot.framework.script.GroovyScriptRunningContext;
import lombok.Getter;

public class GroovyShellContext {

    // 缓存最大值
    private final static int MAX_CACHE_SIZE = 10000;

    @Getter
    private final static GroovyShellContext instance = new GroovyShellContext();


    private GroovyShellContext() {
        if(GroovyScriptRunningContext.getInstance().getMaxCacheSize()!=MAX_CACHE_SIZE) {
            GroovyScriptRunningContext.getInstance().setMaxCacheSize(MAX_CACHE_SIZE);
        }
    }


    public <T> T run(String script, Class<T> returnType, Object... args) {
        return GroovyScriptRunningContext.getInstance().run("run", script, returnType, args);
    }


    public int size(){
        return GroovyScriptRunningContext.getInstance().cacheSize();
    }

    public void clear(){
        GroovyScriptRunningContext.getInstance().clearCache();
    }

}
