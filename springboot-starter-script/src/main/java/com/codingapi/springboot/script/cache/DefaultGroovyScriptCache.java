package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认脚本缓存容器实现：进程内 LRU 缓存
 */
public class DefaultGroovyScriptCache implements GroovyScriptCache {

    private final static int MAX_CACHE_SIZE = 10 * 1024;

    private final Map<String, GroovyScript> cache = new LinkedHashMap<String, GroovyScript>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, GroovyScript> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    @Override
    public void save(GroovyScript script) {
        if (script != null) {
            this.cache.put(script.getKey(), script);
        }
    }

    @Override
    public GroovyScript get(String key) {
        return this.cache.get(key);
    }

    @Override
    public void remove(String key) {
        this.cache.remove(key);
    }

    @Override
    public List<String> keys() {
        return new ArrayList<String>(this.cache.keySet());
    }

    @Override
    public int count() {
        return this.cache.size();
    }

    @Override
    public void clear() {
        this.cache.clear();
    }
}
