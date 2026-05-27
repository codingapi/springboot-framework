package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.GroovyScript;

/**
 * GroovyScript 仓储对象
 */
public interface GroovyScriptRepository {

    /**
     * 保存脚本
     * @param groovyScript 脚本对象
     */
    void save(GroovyScript groovyScript);

    /**
     * 删除脚本
     * @param groovyScript 脚本对象
     */
    void delete(GroovyScript groovyScript);
}
