package com.codingapi.springboot.script;

import com.codingapi.springboot.script.bind.ClassBinder;
import com.codingapi.springboot.script.bind.ClassBinderBuilder;
import com.codingapi.springboot.script.bind.ObjectBinder;
import com.codingapi.springboot.script.cache.GroovyScriptContext;
import com.codingapi.springboot.script.em.TransactionMode;
import com.codingapi.springboot.script.gateway.GroovyMetadataReloadGatewayContext;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.service.GroovyMetadataParserService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 脚本对象
 */
@Setter
@Getter
public class GroovyScript {

    /**
     * 脚本唯一编码
     */
    private final String key;
    /**
     * 脚本内容
     */
    private String script;
    /**
     * 脚本描述信息
     */
    private String description;
    /**
     * 脚本函数名称
     */
    private String method;
    /**
     * 返回数据类型
     */
    private Class<?> returnType;
    /**
     * 绑定数据类型
     */
    private List<ClassBinder> binds;
    /**
     * 请求参数对象
     */
    private List<ClassBinder> requests;


    private GroovyScript(String key,
                         String script,
                         String description,
                         String method,
                         Class<?> returnType,
                         List<ClassBinder> binds,
                         List<ClassBinder> requests) {
        this.key = key;
        this.script = script;
        this.description = description;
        this.method = method;
        this.returnType = returnType;
        this.binds = binds;
        this.requests = requests;

        GroovyScriptContext.getInstance().update(this);
    }

    public static GroovyScript create(String key,
                                      String script,
                                      String description,
                                      String method,
                                      Class<?> returnType,
                                      List<ClassBinder> binds,
                                      List<ClassBinder> requests) {
        return new GroovyScript(key, script, description, method, returnType, binds, requests);
    }


    public static GroovyScript create(String key,
                                      String script,
                                      String description,
                                      String method,
                                      Class<?> returnType,
                                      ClassBinderBuilder binderBuilder,
                                      ClassBinderBuilder requestBuilder) {
        return new GroovyScript(key, script, description, method, returnType, binderBuilder.build(), requestBuilder.build());
    }


    public static GroovyScript createRun(String key,
                                         String script,
                                         Class<?> returnType,
                                         List<ClassBinder> binds) {
        return new GroovyScript(key, script, null, null, returnType, binds, null);
    }

    public static GroovyScript createRun(String key,
                                         String script,
                                         Class<?> returnType,
                                         ClassBinderBuilder binderBuilder) {
        return new GroovyScript(key, script, null, null, returnType, binderBuilder.build(), null);
    }

    public static GroovyScript createRun(String key,
                                         String script,
                                         String description,
                                         Class<?> returnType,
                                         List<ClassBinder> requests) {
        return new GroovyScript(key, script, description, null, returnType, null, requests);
    }

    public static GroovyScript createRun(String key,
                                         String script,
                                         String description,
                                         Class<?> returnType,
                                         ClassBinderBuilder requestBuilder) {
        return new GroovyScript(key, script, description, null, returnType, null, requestBuilder.build());
    }

    public static GroovyScript createInvoke(String key,
                                            String script,
                                            String description,
                                            String method,
                                            Class<?> returnType,
                                            List<ClassBinder> requests) {
        return new GroovyScript(key, script, description, method, returnType, null, requests);
    }

    public static GroovyScript createInvoke(String key,
                                            String script,
                                            String method,
                                            Class<?> returnType,
                                            List<ClassBinder> requests) {
        return new GroovyScript(key, script, null, method, returnType, null, requests);
    }

    public static GroovyScript createInvoke(String key,
                                            String script,
                                            String method,
                                            Class<?> returnType,
                                            ClassBinderBuilder requestBuilder) {
        return new GroovyScript(key, script, null, method, returnType, null, requestBuilder.build());
    }


    public static GroovyScript createInvoke(String key,
                                            String script,
                                            String method,
                                            Class<?> returnType,
                                            List<ClassBinder> binds,
                                            List<ClassBinder> requests) {
        return new GroovyScript(key, script, null, method, returnType, binds, requests);
    }

    public static GroovyScript createInvoke(String key,
                                            String script,
                                            String method,
                                            Class<?> returnType,
                                            ClassBinderBuilder binderBuilder,
                                            ClassBinderBuilder requestBuilder) {
        return new GroovyScript(key, script, null, method, returnType, binderBuilder.build(), requestBuilder.build());
    }

    /**
     * 编译脚本 并缓存
     *
     * @param cache 是否缓存
     */
    public void compile(boolean cache) {
        GroovyScriptRuntimeContext.getInstance().compile(this.script, cache);
    }

    /**
     * 编译脚本 非缓存
     */
    public void compile() {
        GroovyScriptRuntimeContext.getInstance().compile(this.script);
    }

    /**
     * 转化为直接运行时对象
     *
     * @param binds 绑定对象
     * @return 运行时对象
     */
    public <T> T run(TransactionMode transactionMode, List<ObjectBinder> binds) {
        return (T) GroovyScriptRuntimeContext.getInstance().run(this.script, returnType, transactionMode, binds);
    }

    /**
     * 转化为直接运行时对象
     *
     * @param binds 绑定对象
     * @return 运行时对象
     */
    public <T> T run(List<ObjectBinder> binds) {
        return this.run(TransactionMode.DEFAULT, binds);
    }

    /**
     * 转化为直接运行时对象
     *
     * @return 运行时对象
     */
    public <T> T run(TransactionMode transactionMode) {
        return this.run(transactionMode, null);
    }

    /**
     * 转化为直接运行时对象
     *
     * @return 运行时对象
     */
    public <T> T run() {
        return this.run(TransactionMode.DEFAULT, null);
    }

    /**
     * 转化为函数运行时对象
     *
     * @param transactionMode 事务模式
     * @param binds           绑定对象
     * @param args            运行参数
     * @return 运行时对象
     */
    public <T> T invoke(TransactionMode transactionMode, List<ObjectBinder> binds, List<ObjectBinder> requests) {
        return (T) GroovyScriptRuntimeContext.getInstance().invoke(this.method, this.script, this.returnType, transactionMode, binds, requests);
    }

    /**
     * 转化为函数运行时对象
     *
     * @param binds 绑定对象
     * @param requests  运行参数
     * @return 运行时对象
     */
    public <T> T invoke(List<ObjectBinder> binds, List<ObjectBinder> requests) {
        return this.invoke(TransactionMode.DEFAULT, binds, requests);
    }


    /**
     * 转化为函数运行时对象
     *
     * @param requests 运行参数
     * @return 运行时对象
     */
    public <T> T invoke(TransactionMode transactionMode, List<ObjectBinder> requests) {
        return this.invoke(transactionMode, null, requests);
    }

    /**
     * 转化为函数运行时对象
     *
     * @param requests 运行参数
     * @return 运行时对象
     */
    public <T> T invoke(List<ObjectBinder> requests) {
        return this.invoke(TransactionMode.DEFAULT, null, requests);
    }

    /**
     * 转化为函数运行时对象
     *
     * @return 运行时对象
     */
    public <T> T invoke() {
        return this.invoke(TransactionMode.DEFAULT, null, null);
    }

    /**
     * 转化为函数运行时对象
     *
     * @return 运行时对象
     */
    public <T> T invoke(TransactionMode transactionMode) {
        return this.invoke(transactionMode, null, null);
    }


    /**
     * 构建脚本元数据信息
     */
    public GroovyMetadata toMetadata() {
        GroovyMetadataParserService groovyMetaDataParserService = new GroovyMetadataParserService(this);
        GroovyMetadata metadata = groovyMetaDataParserService.parser();
        GroovyMetadataReloadGatewayContext.getInstance().reload(metadata);
        return metadata;
    }


}
