package com.codingapi.springboot.framework.script.request;

import com.codingapi.springboot.framework.script.GroovyMetadataReloadGatewayContext;
import com.codingapi.springboot.framework.script.meta.GroovyMetadata;
import com.codingapi.springboot.framework.script.service.GroovyMetadataParserService;
import lombok.Getter;

import java.util.ArrayList;
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
     * 脚本说明
     */
    private final String description;

    /**
     * 返回数据类型
     */
    private final Class<T> returnType;

    /**
     * 请求参数对象
     */
    private final List<GroovyBindObject> requests;

    /**
     * 绑定数据对象
     */
    private final List<GroovyBindObject> binds;

    /**
     * 脚本元数据信息
     */
    private GroovyMetadata metadata;


    public GroovyRunningScript(String method, String script, String description, Class<T> returnType, List<GroovyBindObject> binds, List<GroovyBindObject> requests) {
        this.method = method;
        this.script = script;
        this.description = description;
        this.returnType = returnType;
        this.requests = requests;
        this.binds = binds;
    }

    public GroovyRunningScript(String method, String script, String description, Class<T> returnType, GroovyBindObjectBuilder bindBuilder, GroovyBindObjectBuilder requestBuilder) {
        this(method, script, description, returnType, bindBuilder != null ? bindBuilder.build() : null, requestBuilder != null ? requestBuilder.build() : null);
    }


    public GroovyRunningScript(String method, String script, String description, Class<T> returnType, GroovyBindObjectBuilder requestBuilder) {
        this(method, script, description, returnType, null, requestBuilder);
    }

    public GroovyRunningScript(String script,  Class<T> returnType, GroovyBindObjectBuilder bindBuilder, GroovyBindObjectBuilder requestBuilder) {
        this("run", script, null, returnType, bindBuilder, requestBuilder);
    }

    public GroovyRunningScript(String script, String description, Class<T> returnType, GroovyBindObjectBuilder bindBuilder, GroovyBindObjectBuilder requestBuilder) {
        this("run", script, description, returnType, bindBuilder, requestBuilder);
    }

    public GroovyRunningScript(String script,String description, Class<T> returnType, GroovyBindObjectBuilder requestBuilder) {
        this("run", script, returnType, null, requestBuilder);
    }

    public GroovyRunningScript(String script,Class<T> returnType, GroovyBindObjectBuilder requestBuilder) {
        this("run", script,null, returnType, null, requestBuilder);
    }

    /**
     * 获取参数
     */
    public Object[] getParams() {
        List<Object> objects = new ArrayList<>();
        if (this.requests != null) {
            for (GroovyBindObject bindObject : this.requests) {
                objects.add(bindObject.getObject());
            }
        }
        return objects.toArray();
    }

    /**
     * 重新设置脚本元数据信息
     *
     * @param metadata 脚本元数据信息
     */
    public void resetMetadata(GroovyMetadata metadata) {
        this.metadata = metadata;
        GroovyMetadataReloadGatewayContext.getInstance().reload(this.metadata);
    }

    /**
     * 构建脚本元数据信息
     */
    public void buildMetadata() {
        GroovyMetadataParserService groovyMetaDataParserService = new GroovyMetadataParserService(this);
        this.metadata = groovyMetaDataParserService.parser();
        GroovyMetadataReloadGatewayContext.getInstance().reload(this.metadata);
    }

}
