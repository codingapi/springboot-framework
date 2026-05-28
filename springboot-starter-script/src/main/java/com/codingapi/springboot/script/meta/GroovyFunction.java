package com.codingapi.springboot.script.meta;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本函数对象
 */
@Getter
public class GroovyFunction {

    /**
     * 对象访问名称
     */
    @Setter
    private String name;
    /**
     * 对象描述信息
     */
    @Setter
    private String description;
    /**
     * 返回类型
     */
    @Setter
    private String returnType;
    /**
     * 参数类型
     */
    private final List<GroovyField> parameters;

    public GroovyFunction() {
        this.parameters = new ArrayList<>();
    }

    public void addParameter(GroovyField parameter) {
        this.parameters.add(parameter);
    }

}
