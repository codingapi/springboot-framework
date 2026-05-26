package com.codingapi.springboot.framework.script.meta;

import com.codingapi.springboot.framework.script.annotation.ScriptType;
import com.codingapi.springboot.framework.script.service.GroovyTypeParser;
import lombok.Getter;
import lombok.Setter;

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
    @Setter
    private String mainMethod;

    /**
     * 返回类型
     */
    @Setter
    private String returnType;

    /**
     * 字段类型
     */
    private final Map<String, GroovyType> types;


    public GroovyMetadata() {
        this.types = new HashMap<>();
        this.requests = new ArrayList<>();
        this.binds = new ArrayList<>();
    }

    /**
     * 通过class构建 脚本类型数据
     * @param clazz class类型
     */
    public void buildType(Class<?> clazz) {
        String dataType = clazz.getSimpleName();
        GroovyType groovyType = this.types.get(dataType);
        if (groovyType == null) {
            GroovyTypeParser groovyTypeParser = new GroovyTypeParser(clazz, this);
            groovyType = groovyTypeParser.parser();
            groovyType.setDataType(dataType);
            ScriptType scriptType = clazz.getAnnotation(ScriptType.class);
            if(scriptType!=null) {
                groovyType.setDescription(scriptType.description());
            }
            this.put(dataType, groovyType);
        }
    }

    /**
     * 增加请求参数数据对象
     * @param request 请求参数
     */
    public void addRequest(GroovyField request) {
        this.requests.add(request);
    }

    /**
     * 更新流程类型数据
     * @param dataType 数据类型
     * @param groovyType 脚本类型
     */
    public void put(String dataType,GroovyType groovyType){
        this.types.put(dataType, groovyType);
    }


    /**
     * 获取流程类型数据
     * @param dataType 数据类型
     * @return 脚本类型
     */
    public GroovyType getType(String dataType){
        return this.types.get(dataType);
    }


    /**
     * 增加绑定数据对象
     * @param bind 绑定数据
     */
    public void addBind(GroovyField bind) {
        this.binds.add(bind);
    }

}
