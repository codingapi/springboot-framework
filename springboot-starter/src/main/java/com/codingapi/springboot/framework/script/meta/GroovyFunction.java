package com.codingapi.springboot.framework.script.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本函数对象
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroovyFunction {

    /**
     * 对象访问名称
     */
    private String name;
    /**
     * 对象描述信息
     */
    private String description;
    /**
     * 返回类型
     */
    private GroovyType returnType;
    /**
     * 参数类型
     */
    private List<GroovyType> parameters;


    public void addParameter(GroovyType parameter){
        if(this.parameters ==null){
            this.parameters = new ArrayList<>();
        }
        this.parameters.add(parameter);
    }

}
