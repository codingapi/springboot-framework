package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import lombok.Getter;

import java.util.List;

/**
 * 脚本数据缓存上下文管理对象
 * 缓存容器支持替换（如 Redis 共享缓存实现），默认为进程内 LRU 缓存
 */
public class GroovyScriptCacheContext {

    @Getter
    private final static GroovyScriptCacheContext instance = new GroovyScriptCacheContext();

    private GroovyScriptCache cache = new DefaultGroovyScriptCache();

    private GroovyScriptCacheContext() {
    }

    /**
     * 替换缓存容器实现，入参为 null 时回退为默认 LRU 实现
     *
     * @param cache 缓存容器实现
     */
    public void setCache(GroovyScriptCache cache) {
        this.cache = (cache != null) ? cache : new DefaultGroovyScriptCache();
    }

    /**
     * 更新脚本缓存
     *
     * @param script 脚本对象
     */
    public void save(GroovyScript script) {
        this.cache.save(script);
    }


    /**
     * 保存缓存数据
     *
     * @param script 脚本对象
     */
    public void cache(GroovyScript script) {
        this.cache.save(script);
    }


    /**
     * 移除脚本缓存（仅作用于本地缓存容器）
     *
     * @param key 脚本key
     */
    public void remove(String key) {
        this.cache.remove(key);
    }

    /**
     * 获取脚本keys
     */
    public List<String> keys() {
        return this.cache.keys();
    }

    /**
     * 脚本总数量
     *
     * @return 总数量
     */
    public int count() {
        return this.cache.count();
    }

    /**
     * 获取脚本对象
     *
     * @param key 脚本key
     * @return 脚本对象
     */
    public GroovyScript getGroovyScript(String key) {
        GroovyScript groovyScript = this.cache.get(key);
        if (groovyScript == null) {
            // 临时的数据不存到cache对象下
            groovyScript = TempGroovyScriptContext.getInstance().getGroovyScript(key);
            if (groovyScript == null) {
                groovyScript = GroovyScriptRepositoryContext.getInstance().get(key);
                if (groovyScript != null) {
                    this.cache.save(groovyScript);
                }
            }
        }
        return groovyScript;
    }

    /**
     * 获取脚本元数据信息
     *
     * @param key 脚本keu
     * @return 元数据信息
     */
    public GroovyMetadata getGroovyMetadata(String key) {
        GroovyScript script = this.getGroovyScript(key);
        if (script != null) {
            return script.toMetadata();
        }
        return null;
    }

    /**
     * 获取脚本内容
     *
     * @param key 脚本key
     * @return 脚本数据
     */
    public String getScript(String key) {
        GroovyScript script = this.getGroovyScript(key);
        if (script != null) {
            return script.getScript();
        }
        return "";
    }

    /**
     * 批量加载脚本
     *
     * @param scriptList 脚本数据
     */
    public void setBatchCache(List<GroovyScript> scriptList) {
        if (scriptList != null) {
            for (GroovyScript script : scriptList) {
                if (script != null) {
                    this.cache.save(script);
                }
            }
        }
    }

    /**
     * 编译脚本
     *
     * @param cache 是否缓存
     */
    public void compileAll(boolean cache) {
        for (String key : this.cache.keys()) {
            GroovyScript groovyScript = this.cache.get(key);
            if (groovyScript != null) {
                groovyScript.compile(cache);
            }
        }
    }


    /**
     * 清空脚本数据
     */
    public void clear() {
        this.cache.clear();
    }
}
