package com.codingapi.springboot.framework.script;

import com.codingapi.springboot.framework.crypto.SHA256;
import com.codingapi.springboot.framework.script.request.GroovyBindObject;
import com.codingapi.springboot.framework.script.request.GroovyRunningScript;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * groovy script 执行上下文对象
 */
public class GroovyScriptRunningContext {

    private final GroovyShell shell;

    private final Map<String, Script> cache;

    @Getter
    private final int maxCacheSize;

    public static final int MAX_CACHE_SIZE = 1000;

    @Getter
    private static GroovyScriptRunningContext instance = new GroovyScriptRunningContext(MAX_CACHE_SIZE);

    private GroovyScriptRunningContext(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(getClass().getClassLoader());
        this.shell = new GroovyShell(groovyClassLoader);
        this.cache = new LinkedHashMap<>(16, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Script> eldest) {
                return size() > maxCacheSize;
            }
        };
    }

    /**
     * 更新最大的缓存大小
     */
    public void setMaxCacheSize(int maxCacheSize) {
        instance = new GroovyScriptRunningContext(maxCacheSize);
    }

    /**
     * 清空缓存对象
     */
    public void clearCache() {
        this.cache.clear();
    }

    /**
     * 缓存大小
     */
    public int cacheSize(){
        return this.cache.size();
    }

    /**
     * 编译脚本
     *
     * @param script 脚本内容
     * @param cache  是否缓存
     */
    public void compile(String script, boolean cache) {
        if (cache) {
            String key = SHA256.sha256(script);
            Script runtime = this.cache.get(key);
            if (runtime == null) {
                runtime = this.shell.parse(script);
                this.cache.put(key, runtime);
            }
        } else {
            this.shell.parse(script);
        }
    }


    /**
     * 编译脚本，非缓存模式
     * @param script 脚本内容
     */
    public void compile(String script) {
        this.compile(script, false);
    }

    /**
     * 执行脚本
     * @param method 函数名称
     * @param script 脚本内容
     * @param returnType 返回类型
     * @param binds 绑定数据对象
     * @param args 函数参数
     * @return 返回数据
     */
    public <T> T run(String method, String script, Class<T> returnType, List<GroovyBindObject> binds, Object... args) {
        String key = SHA256.sha256(script);
        Script runtime = this.cache.get(key);
        if (runtime == null) {
            runtime = this.shell.parse(script);
            this.cache.put(key, runtime);
        }

        if (binds != null && !binds.isEmpty()) {
            for (GroovyBindObject groovyBindObject : binds) {
                runtime.setProperty(groovyBindObject.getKey(), groovyBindObject.getObject());
            }
        }
        return (T) runtime.invokeMethod(method, args);
    }

    /**
     * 运行脚本
     * @param method 函数名称
     * @param script 脚本内容
     * @param returnType 返回类型
     * @param args 函数参数
     * @return 返回数据
     */
    public <T> T run(String method, String script, Class<T> returnType, Object... args) {
        return this.run(method,script,returnType,null,args);
    }


    /**
     * 执行脚本
     * @param request 脚本参数
     * @return 返回数据
     */
    public <T> T run(GroovyRunningScript<T> request) {
        return this.run(request.getMethod(),request.getScript(),request.getReturnType(),request.getBinds(),request.getParams());
    }

}
