package com.codingapi.springboot.framework.script.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本对象类型
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroovyType {

    /**
     * 访问名称
     */
    @Setter
    private String name;

    /**
     * 描述信息
     */
    @Setter
    private String description;

    /**
     * 数据类型
     */
    @Setter
    private Class<?> dataType;

    /**
     * 属性列表
     */
    private List<GroovyType> fields;

    /**
     * 函数列表
     */
    private List<GroovyFunction> functions;


    public void initFields(){
        if(this.fields ==null){
            this.fields = new ArrayList<>();
        }
    }

    public String getDataClassName(){
        return dataType.getSimpleName();
    }


    public void initFunctions(){
        if(this.functions==null){
            this.functions = new ArrayList<>();
        }
    }

    public void addFunction(GroovyFunction function){
        if(this.functions==null){
            this.functions = new ArrayList<>();
        }

        this.functions.add(function);
    }


    public void addField(GroovyType field){
        if(this.fields ==null){
            this.fields = new ArrayList<>();
        }
        this.fields.add(field);
    }


    /**
     * 保留函数和属性
     */
    public GroovyType cacheFieldsAndMethods(){
        GroovyType cache = new GroovyType();
        cache.functions = this.functions;
        cache.fields = this.fields;
        cache.dataType = this.dataType;
        return cache;
    }

}
