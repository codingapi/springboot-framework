package com.codingapi.springboot.framework.script.meta;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本对象类型
 */
@Getter
public class GroovyType {

    /**
     * 对象描述
     */
    @Setter
    private String description;

    /**
     * 数据类型
     */
    @Setter
    private String dataType;

    /**
     * 属性列表
     */
    private final List<GroovyField> fields;

    /**
     * 函数列表
     */
    private final List<GroovyFunction> functions;

    public GroovyType() {
        this.fields = new ArrayList<>();
        this.functions = new ArrayList<>();
    }


    public void addFunction(GroovyFunction function) {
        this.functions.add(function);
    }


    public void addField(GroovyField field) {
        this.fields.add(field);
    }

}
