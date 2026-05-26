package com.codingapi.springboot.framework.script.service;

import com.codingapi.springboot.framework.script.meta.GroovyType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 脚本对象临时缓存对象
 */
class TempGroovyTypeCache {

    @Getter
    private final static TempGroovyTypeCache instance = new TempGroovyTypeCache();

    private final ThreadLocal<Map<Class<?>, GroovyType>> cache;


    private TempGroovyTypeCache() {
        this.cache = new ThreadLocal<>();
    }

    public void clear() {
        this.cache.remove();
    }


    public void set(Class<?> key, GroovyType object) {
        Map<Class<?>, GroovyType> map = this.cache.get();
        if (map == null) {
            map = new HashMap<>();
            this.cache.set(map);
        }
        map.put(key, object);
    }


    public GroovyType getOrCreate(Class<?> clazz) {
        Map<Class<?>, GroovyType> map = this.cache.get();
        if (map == null) {
            map = new HashMap<>();
            this.cache.set(map);
        }
        GroovyType object = map.get(clazz);
        if (object == null) {
            GroovyTypeParser parser = new GroovyTypeParser(clazz);
            object = parser.parser();
            map.put(clazz, object);
        }
        return object.cacheFieldsAndMethods();
    }


}
