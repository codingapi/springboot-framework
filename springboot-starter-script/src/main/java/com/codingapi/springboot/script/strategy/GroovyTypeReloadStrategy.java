package com.codingapi.springboot.script.strategy;

import com.codingapi.springboot.script.meta.GroovyType;

/**
 *  脚本元数据刷新加载对象
 */
public interface GroovyTypeReloadStrategy {

    boolean support(Class<?> clazz);

    /**
     * 刷新元数据结构
     * @param tag 数据标记
     * @param groovyType 类型元数据结构（扫描后）
     */
    void reload(String tag,GroovyType groovyType);

}
