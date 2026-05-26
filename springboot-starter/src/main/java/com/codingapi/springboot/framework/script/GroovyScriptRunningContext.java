package com.codingapi.springboot.framework.script;

import com.codingapi.springboot.framework.script.request.GroovyBindObject;
import com.codingapi.springboot.framework.script.request.GroovyRunningScript;
import lombok.Getter;

import java.util.List;

/**
 * groovy script 执行上下文对象,事务模式为可写入
 */
public class GroovyScriptRunningContext {

    private final GroovyScriptRunner scriptRunner;

    public static final int MAX_CACHE_SIZE = 1000;

    @Getter
    private static GroovyScriptRunningContext instance = new GroovyScriptRunningContext(MAX_CACHE_SIZE);

    private GroovyScriptRunningContext(int maxCacheSize) {
        this.scriptRunner = new GroovyScriptRunner(maxCacheSize);
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
        this.scriptRunner.clearCache();
    }

    /**
     * 缓存大小
     */
    public int cacheSize() {
        return this.scriptRunner.cacheSize();
    }

    /**
     * 编译脚本
     *
     * @param script 脚本内容
     * @param cache  是否缓存
     */
    public void compile(String script, boolean cache) {
        this.scriptRunner.compile(script, cache);
    }


    /**
     * 编译脚本，非缓存模式
     *
     * @param script 脚本内容
     */
    public void compile(String script) {
        this.compile(script, false);
    }

    /**
     * 执行函数脚本
     *
     * @param script     脚本内容
     * @param returnType 返回类型
     * @return 返回数据
     */
    public <T> T run(String script, Class<T> returnType) {
        return this.scriptRunner.run(script, returnType, null);
    }

    /**
     * 执行函数脚本
     *
     * @param script     脚本内容
     * @param returnType 返回类型
     * @param binds      绑定数据对象
     * @return 返回数据
     */
    public <T> T run(String script, Class<T> returnType, List<GroovyBindObject> binds) {
        return this.scriptRunner.run(script, returnType, binds);
    }

    /**
     * 执行函数脚本
     *
     * @param method     函数名称
     * @param script     脚本内容
     * @param returnType 返回类型
     * @param binds      绑定数据对象
     * @param args       函数参数
     * @return 返回数据
     */
    public <T> T invoke(String method, String script, Class<T> returnType, List<GroovyBindObject> binds, Object... args) {
        return this.scriptRunner.invoke(method, script, returnType, binds, args);
    }

    /**
     * 运行脚本
     *
     * @param method     函数名称
     * @param script     脚本内容
     * @param returnType 返回类型
     * @param args       函数参数
     * @return 返回数据
     */
    public <T> T invoke(String method, String script, Class<T> returnType, Object... args) {
        return this.invoke(method, script, returnType, null, args);
    }


    /**
     * 执行脚本
     *
     * @param request 脚本参数
     * @return 返回数据
     */
    public <T> T invoke(GroovyRunningScript<T> request) {
        return this.invoke(request.getMethod(), request.getScript(), request.getReturnType(), request.getBinds(), request.getParams());
    }

}
