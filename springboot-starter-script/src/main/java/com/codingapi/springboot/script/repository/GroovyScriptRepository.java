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
     * @param key 脚本key
     */
    void delete(String key);


    /**
     * 获取脚本
     * @param key 脚本key
     */
    GroovyScript get(String key);
}
