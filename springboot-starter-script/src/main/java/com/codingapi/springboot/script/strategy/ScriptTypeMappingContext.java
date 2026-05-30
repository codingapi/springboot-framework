package com.codingapi.springboot.script.strategy;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *  脚本类型映射上下文对象
 */
public class ScriptTypeMappingContext {

    @Getter
    private final static ScriptTypeMappingContext instance = new ScriptTypeMappingContext();

    private final List<ScriptTypeMapping> mappings;

    private ScriptTypeMappingContext() {
        this.mappings = new ArrayList<>();
    }

    /**
     * 添加类型映射
     * @param mapping 映射对象
     */
    public void addMapping(ScriptTypeMapping mapping){
        this.mappings.add(mapping);
    }


    /**
     * 清空策略
     */
    public void clear(){
        this.mappings.clear();
    }


    /**
     * 映射
     * @param target 类对象
     * @return 目标类对象
     */
    public Class<?> mapping(Class<?> target) {
        for (ScriptTypeMapping mapping : this.mappings) {
            if (mapping.support(target)) {
                return mapping.mapping(target);
            }
        }
        return target;
    }


}
