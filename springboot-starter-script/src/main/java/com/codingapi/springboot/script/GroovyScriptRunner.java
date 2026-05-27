package com.codingapi.springboot.script;

import com.codingapi.springboot.framework.crypto.SHA256;
import com.codingapi.springboot.script.request.GroovyBindObject;
import com.codingapi.springboot.script.request.GroovyRunningScript;
import com.codingapi.springboot.framework.transaction.TransactionManagerContext;
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
public class GroovyScriptRunner {

    private final GroovyShell shell;

    private final Map<String, Script> cache;

    @Getter
    private final int maxCacheSize;

    @Getter
    private final TransactionMode transactionMode;


    public enum TransactionMode {
        // 默认不处理
        DEFAULT,
        // 事务提交模式
        COMMIT,
        // 事务只读模式
        READONLY
    }


    public GroovyScriptRunner(int maxCacheSize) {
        this(maxCacheSize, TransactionMode.DEFAULT);
    }

    public GroovyScriptRunner(int maxCacheSize, TransactionMode transactionMode) {
        this.maxCacheSize = maxCacheSize;
        this.transactionMode = transactionMode;
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
     * 清空缓存对象
     */
    public void clearCache() {
        this.cache.clear();
    }

    /**
     * 缓存大小
     */
    public int cacheSize() {
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
     *
     * @param script 脚本内容
     */
    public void compile(String script) {
        this.compile(script, false);
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
        String key = SHA256.sha256(script);
        Script runtime = this.cache.get(key);
        if (runtime == null) {
            runtime = this.shell.parse(script);
            this.cache.put(key, runtime);
        }

        if (binds != null && !binds.isEmpty()) {
            for (GroovyBindObject groovyBindObject : binds) {
                runtime.setProperty(groovyBindObject.getName(), groovyBindObject.getObject());
            }
        }

        // 事务制只读模式
        if (transactionMode == TransactionMode.READONLY) {
            Script finalRuntime = runtime;
            return TransactionManagerContext.getInstance().readOnly(() -> {
                return (T) finalRuntime.invokeMethod(method, args);
            });
        }

        // 事务制提交模式
        if (transactionMode == TransactionMode.COMMIT) {
            Script finalRuntime = runtime;
            return TransactionManagerContext.getInstance().commit(() -> {
                return (T) finalRuntime.invokeMethod(method, args);
            });
        }

        // 默认处理模式
        return (T) runtime.invokeMethod(method, args);
    }


    /**
     * 执行脚本
     *
     * @param script     脚本
     * @param returnType 返回数据类型
     * @param binds      绑定对象
     * @return 返回数据
     */
    public <T> T run(String script, Class<T> returnType, List<GroovyBindObject> binds) {
        String key = SHA256.sha256(script);
        Script runtime = this.cache.get(key);
        if (runtime == null) {
            runtime = this.shell.parse(script);
            this.cache.put(key, runtime);
        }

        if (binds != null && !binds.isEmpty()) {
            for (GroovyBindObject groovyBindObject : binds) {
                runtime.setProperty(groovyBindObject.getName(), groovyBindObject.getObject());
            }
        }

        // 事务制只读模式
        if (transactionMode == TransactionMode.READONLY) {
            Script finalRuntime = runtime;
            return TransactionManagerContext.getInstance().readOnly(() -> {
                return (T) finalRuntime.run();
            });
        }

        // 事务制提交模式
        if (transactionMode == TransactionMode.COMMIT) {
            Script finalRuntime = runtime;
            return TransactionManagerContext.getInstance().commit(() -> {
                return (T) finalRuntime.run();
            });
        }

        // 默认处理模式
        return (T) runtime.run();
    }


    /**
     * 执行脚本
     *
     * @param script     脚本
     * @param returnType 返回类型
     * @return 返回数据
     */
    public <T> T run(String script, Class<T> returnType) {
        return this.run(script, returnType, null);
    }

    /**
     * 执行函数脚本
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
     * 执行函数脚本
     *
     * @param request 脚本参数
     * @return 返回数据
     */
    public <T> T invoke(GroovyRunningScript<T> request) {
        return this.invoke(request.getMethod(), request.getScript(), request.getReturnType(), request.getBinds(), request.getParams());
    }

}
