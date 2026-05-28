package com.codingapi.springboot.script;

import com.codingapi.springboot.script.cache.GroovyScriptCacheContext;
import com.codingapi.springboot.script.gateway.GroovyMetadataReloadGatewayContext;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.service.GroovyMetadataParserService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * 脚本对象
 */
@Getter
@AllArgsConstructor
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
    private Map<String, Class<?>> binds;
    /**
     * 请求参数对象
     */
    private Map<String, Class<?>> requests;

    /**
     * 一级类型
     */
    private String typeOne;

    /**
     * 二级类型
     */
    private String typeTwo;

    /**
     * 备注信息
     */
    private String remark;


    private GroovyScript(String key) {
        this.key = key;
    }

    public static Builder builder(String key){
        return new Builder(key);
    }

    public static class Builder{
        private final GroovyScript script;

        public Builder(String key) {
            this.script = new GroovyScript(key);
        }

        public Builder script(String script){
            this.script.script = script;
            return this;
        }

        public Builder description(String description){
            this.script.description = description;
            return this;
        }

        public Builder method(String method){
            this.script.method = method;
            return this;
        }

        public Builder typeOne(String typeOne){
            this.script.typeOne = typeOne;
            return this;
        }

        public Builder typeTwo(String typeTwo){
            this.script.typeTwo = typeTwo;
            return this;
        }

        public Builder remark(String remark){
            this.script.remark = remark;
            return this;
        }

        public Builder returnType(Class<?> returnType){
            this.script.returnType = returnType;
            return this;
        }

        public Builder binds(Map<String,Class<?>> binds){
            this.script.binds = binds;
            return this;
        }

        public Builder requests(Map<String,Class<?>> requests){
            this.script.requests = requests;
            return this;
        }

        public GroovyScript build(){
            return this.script;
        }

    }


    /**
     * 保存缓存并持久化
     */
    public void save() {
        GroovyScriptCacheContext.getInstance().update(this);
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
    public <T> T run(TransactionMode transactionMode, Map<String, Object> binds) {
        return (T) GroovyScriptRuntimeContext.getInstance().run(this.script, returnType, transactionMode, binds);
    }

    /**
     * 转化为直接运行时对象
     *
     * @param binds 绑定对象
     * @return 运行时对象
     */
    public <T> T run(Map<String, Object> binds) {
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
     * @param requests        运行参数
     * @return 运行时对象
     */
    public <T> T invoke(TransactionMode transactionMode, Map<String, Object> binds, Object... requests) {
        return (T) GroovyScriptRuntimeContext.getInstance().invoke(this.method, this.script, this.returnType, transactionMode, binds, requests);
    }


    /**
     * 转化为函数运行时对象
     *
     * @param binds    绑定对象
     * @param requests 运行参数
     * @return 运行时对象
     */
    public <T> T invoke(Map<String, Object> binds, Object... requests) {
        return this.invoke(TransactionMode.DEFAULT, binds, requests);
    }


    /**
     * 转化为函数运行时对象
     *
     * @param requests 运行参数
     * @return 运行时对象
     */
    public <T> T invoke(TransactionMode transactionMode, Object... requests) {
        return this.invoke(transactionMode, null, requests);
    }

    /**
     * 转化为函数运行时对象
     *
     * @param requests 运行参数
     * @return 运行时对象
     */
    public <T> T invoke(Object... requests) {
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
