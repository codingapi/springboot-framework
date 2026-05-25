package com.codingapi.springboot.framework.script.request;

import com.codingapi.springboot.framework.script.meta.GroovyMetadata;
import com.codingapi.springboot.framework.script.service.GroovyMetadataParserService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 脚本执行对象
 *
 * @param <T> 返回格式
 */
@Getter
public class GroovyRunningScript<T> {

    /**
     * 脚本函数名称
     */
    private final String method;

    /**
     * 脚本代码
     */
    private final String script;

    /**
     * 返回数据类型
     */
    private final Class<T> returnType;

    /**
     * 请求参数对象
     */
    private final List<Object> requests;

    /**
     * 绑定数据对象
     */
    private final List<GroovyBindObject> binds;

    /**
     * 脚本元数据信息
     */
    @Setter
    private GroovyMetadata groovyMetadata;


    public GroovyRunningScript(String method, String script, Class<T> returnType, GroovyMetadata groovyMetadata, List<GroovyBindObject> binds, Object... requests) {
        this.method = method;
        this.script = script;
        this.returnType = returnType;
        this.requests = new ArrayList<>();
        this.binds = binds;
        if (requests != null) {
            this.requests.addAll(Arrays.asList(requests));
        }
        this.groovyMetadata = groovyMetadata;

        if (this.groovyMetadata == null) {
            this.resetMetaData();
        }
    }

    public GroovyRunningScript(String script, Class<T> returnType, GroovyMetadata groovyMetadata, List<GroovyBindObject> binds, Object... request) {
        this("run", script, returnType, groovyMetadata, binds, request);
    }

    public GroovyRunningScript(String script, Class<T> returnType, Object... request) {
        this("run", script, returnType, null, null, request);
    }

    /**
     * 获取参数
     */
    public Object[] getParams() {
        return this.requests.toArray();
    }

    /**
     * 添加绑定对象
     *
     * @param key  绑定key
     * @param bind 绑定对象
     */
    public void addBindObject(String key, Object bind) {
        this.binds.add(new GroovyBindObject(key, bind));
        this.resetMetaData();
    }

    /**
     * 重置元数据对象
     */
    private void resetMetaData() {
        GroovyMetadataParserService groovyMetaDataParserService = new GroovyMetadataParserService(this);
        this.groovyMetadata = groovyMetaDataParserService.parser();
    }

}
