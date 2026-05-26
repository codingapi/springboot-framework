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
            this.types.put(dataType, groovyType);
        }
    }

    public void addRequest(GroovyField request) {
        this.requests.add(request);
    }


    public GroovyType getType(String dataType){
        return this.types.get(dataType);
    }


    public void addBind(GroovyField bind) {
        this.binds.add(bind);
    }

}
