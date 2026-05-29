package com.codingapi.springboot.script.meta;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.strategy.ScriptTypeMappingContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 脚本对象元数据结构
 */
@Getter
public class GroovyMetadata {

    /**
     * 请求参数
     */
    private final List<GroovyField> requests;

    /**
     * 绑定参数
     */
    private final List<GroovyField> binds;

    /**
     * 程序主函数名称
     */
    private final String mainMethod;

    /**
     * 返回类型
     */
    private final String returnType;

    /**
     * 字段类型
     */
    private final Map<String, GroovyType> types;

    /**
     * 脚本说明
     */
    private final String description;


    public GroovyMetadata(GroovyScript groovyScript) {
        this.types = new HashMap<>();
        this.requests = new ArrayList<>();
        this.binds = new ArrayList<>();
        this.description = groovyScript.getDescription();
        this.mainMethod = groovyScript.getMethod();
        this.returnType = ScriptTypeMappingContext.getInstance().mapping(groovyScript.getReturnType()).getSimpleName();
    }


    /**
     * 增加请求参数数据对象
     *
     * @param request 请求参数
     */
    public void addRequest(GroovyField request) {
        this.requests.add(request);
    }

    /**
     * 更新流程类型数据
     *
     * @param dataType   数据类型
     * @param groovyType 脚本类型
     */
    public void put(String dataType, GroovyType groovyType) {
        this.types.put(dataType, groovyType);
    }


    /**
     * 获取流程类型数据
     *
     * @param dataType 数据类型
     * @return 脚本类型
     */
    public GroovyType getType(String dataType) {
        return this.types.get(dataType);
    }


    /**
     * 增加绑定数据对象
     *
     * @param bind 绑定数据
     */
    public void addBind(GroovyField bind) {
        this.binds.add(bind);
    }

}
