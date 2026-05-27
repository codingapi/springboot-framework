package com.codingapi.springboot.script;

import com.codingapi.springboot.script.meta.GroovyMetadata;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 脚本元数据上下文管理对象
 */
public class GroovyMetadataContext {

    @Getter
    private final static GroovyMetadataContext instance = new GroovyMetadataContext();

    private final Map<String,GroovyMetadata> metadataCache;

    private GroovyMetadataContext(){
        this.metadataCache = new HashMap<>();
    }

    public void put(String key,GroovyMetadata metadata){
        this.metadataCache.put(key,metadata);
    }

    public List<String> metadataKeys(){
        return this.metadataCache.keySet().stream().toList();
    }

    public GroovyMetadata getMetadata(String key){
        return this.metadataCache.get(key);
    }
}
