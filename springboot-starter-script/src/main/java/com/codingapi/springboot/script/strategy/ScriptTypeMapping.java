package com.codingapi.springboot.script.strategy;

/**
 *  脚本类型关系映射
 */
public interface ScriptTypeMapping {

    /**
     * 是否匹配
     * @param target 类型
     */
    boolean support(Class<?> target);


    /**
     * 转化后的类型
     * @param target 类型
     * @return 目标类型
     */
    Class<?> mapping(Class<?> target);

}
