package com.codingapi.springboot.script.strategy;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyType;

/**
 * 脚本元数据调整策略
 */
public interface GroovyTypeFixStrategy {

    boolean support(Class<?> clazz);

    /**
     * 调整元数据结构
     *
     * @param groovyScript 脚本对象
     * @param groovyType   类型元数据结构（扫描后）
     */
    void fix(GroovyScript groovyScript, GroovyType groovyType);

}
