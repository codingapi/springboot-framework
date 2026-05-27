package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 脚本数据上下文管理对象
 */
public class GroovyScriptCacheContext {

    @Getter
    private final static GroovyScriptCacheContext instance = new GroovyScriptCacheContext();

    private final Map<String, GroovyScript> cache;

    private GroovyScriptCacheContext() {
        this.cache = new LinkedHashMap<>(16, 0.75f);
    }

    /**
     * 更新脚本缓存
     *
     * @param script 脚本对象
     */
    public void update(GroovyScript script) {
        if (script != null) {
            this.cache.put(script.getKey(), script);
            GroovyScriptRepositoryContext.getInstance().save(script);
        }
    }

    /**
     * 删除脚本
     *
     * @param key 脚本key
     */
    public void remove(String key) {
        GroovyScript groovyScript = this.getGroovyScript(key);
        if (groovyScript != null) {
            this.cache.remove(key);
            GroovyScriptRepositoryContext.getInstance().delete(groovyScript);
        }
    }

    /**
     * 获取脚本keys
     */
    public List<String> keys() {
        return this.cache.keySet().stream().toList();
    }

    /**
     * 脚本总数量
     *
     * @return 总数量
     */
    public int count() {
        return this.cache.size();
    }

    /**
     * 获取脚本对象
     *
     * @param key 脚本key
     * @return 脚本对象
     */
    public GroovyScript getGroovyScript(String key) {
        return this.cache.get(key);
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
     * 获取脚本编辑信息
     *
     * @param key 脚本可以
     */
    public Map<String, Object> getEditorScript(String key) {
        Map<String, Object> map = new HashMap<>();
        GroovyScript script = this.getGroovyScript(key);
        if (script != null) {
            map.put("script", script.getScript());
            map.put("metadata", script.toMetadata());
        }
        return map;
    }


    /**
     * 批量加载脚本
     *
     * @param scriptList 脚本数据
     */
    public void loadAll(List<GroovyScript> scriptList) {
        if (scriptList != null) {
            for (GroovyScript script : scriptList) {
                if (script != null) {
                    this.cache.put(script.getKey(), script);
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
        for (GroovyScript script : this.cache.values()) {
            script.compile(cache);
        }
    }


    /**
     * 清空脚本数据
     */
    public void clear(){
        this.cache.clear();
    }
}
