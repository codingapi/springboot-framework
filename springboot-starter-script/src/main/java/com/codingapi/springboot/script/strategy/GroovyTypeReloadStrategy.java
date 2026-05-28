package com.codingapi.springboot.script.strategy;

import com.codingapi.springboot.script.meta.GroovyType;

/**
 *  脚本元数据刷新加载对象
 */
public interface GroovyTypeReloadStrategy {

    boolean support(Class<?> clazz);

    void reload(GroovyType groovyType);

}
