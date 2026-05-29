package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 脚本数据缓存上下文管理对象
 */
public class GroovyScriptCacheContext {

    @Getter
    private final static GroovyScriptCacheContext instance = new GroovyScriptCacheContext();

    private final Map<String, GroovyScript> cache;

    private final static int MAX_CACHE_SIZE = 10 * 1024;

    private GroovyScriptCacheContext() {
        this.cache = new LinkedHashMap<String,GroovyScript>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, GroovyScript> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    /**
     * 更新脚本缓存
     *
     * @param script 脚本对象
     */
    public void save(GroovyScript script) {
        if (script != null) {
            this.cache.put(script.getKey(), script);
        }
    }


    /**
     * 保存缓存数据
     *
     * @param script 脚本对象
     */
    public void cache(GroovyScript script) {
        if (script != null) {
            this.cache.put(script.getKey(), script);
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
        }
    }

    /**
     * 获取脚本keys
     */
    public List<String> keys() {
        return new ArrayList<>(this.cache.keySet());
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
        GroovyScript groovyScript = this.cache.get(key);
        if (groovyScript == null) {
            // 临时的数据不存到cache对象下
            groovyScript = TempGroovyScriptContext.getInstance().getGroovyScript(key);
            if (groovyScript == null) {
                groovyScript = GroovyScriptRepositoryContext.getInstance().get(key);
                if (groovyScript != null) {
                    this.cache.put(key, groovyScript);
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
    public void clear() {
        this.cache.clear();
    }
}
