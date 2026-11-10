package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;

import java.util.List;

/**
 * 脚本缓存容器接口
 * 默认实现为进程内 LRU 缓存（{@link DefaultGroovyScriptCache}），
 * 多实例部署时可替换为共享存储实现（如 Redis 缓存），解决跨进程数据一致性问题
 */
public interface GroovyScriptCache {

    /**
     * 保存脚本对象
     *
     * @param script 脚本对象
     */
    void save(GroovyScript script);

    /**
     * 获取脚本对象
     *
     * @param key 脚本key
     * @return 脚本对象，不存在时返回 null
     */
    GroovyScript get(String key);

    /**
     * 移除脚本对象
     *
     * @param key 脚本key
     */
    void remove(String key);

    /**
     * 获取脚本keys
     *
     * @return 脚本key列表
     */
    List<String> keys();

    /**
     * 脚本总数量
     *
     * @return 总数量
     */
    int count();

    /**
     * 清空缓存
     */
    void clear();
}
