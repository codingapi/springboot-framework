package com.codingapi.springboot.script.meta;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.annotation.ScriptType;
import com.codingapi.springboot.script.parser.GroovyTypeParser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 脚本对象元数据结构
 */
public class GroovyMetadata {

    /**
     * 请求参数
     */
    @Getter
    private final List<GroovyField> requests;

    /**
     * 绑定参数
     */
    @Getter
    private final List<GroovyField> binds;

    /**
     * 程序主函数名称
     */
    @Getter
    private String mainMethod;

    /**
     * 返回类型
     */
    @Getter
    private String returnType;

    /**
     * 字段类型
     */
    @Getter
    private final Map<String, GroovyType> types;

    /**
     * 脚本说明
     */
    @Getter
    private String description;


    private transient final GroovyScript groovyScript;


    public GroovyMetadata(GroovyScript groovyScript) {
        this.groovyScript = groovyScript;
        this.types = new HashMap<>();
        this.requests = new ArrayList<>();
        this.binds = new ArrayList<>();
        this.description = groovyScript.getDescription();
        this.mainMethod = groovyScript.getMethod();
        this.returnType = groovyScript.getReturnType().getSimpleName();
    }

    /**
     * 通过class构建 脚本类型数据
     *
     * @param clazz class类型
     */
    public void buildType(Class<?> clazz) {
        String dataType = clazz.getSimpleName();
        GroovyType groovyType = this.types.get(dataType);
        if (groovyType == null) {
            GroovyTypeParser groovyTypeParser = new GroovyTypeParser(clazz, this);
            groovyType = groovyTypeParser.parser(this.groovyScript);
            groovyType.setDataType(dataType);
            ScriptType scriptType = clazz.getAnnotation(ScriptType.class);
            if (scriptType != null) {
                groovyType.setDescription(scriptType.description());
            }
            this.put(dataType, groovyType);
        }
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
