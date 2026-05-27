package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 脚本数据上下文管理对象
 */
public class GroovyScriptContext {

    @Getter
    private final static GroovyScriptContext instance = new GroovyScriptContext();

    private final Map<String, GroovyScript> cache;

    private GroovyScriptContext() {
        this.cache = new LinkedHashMap<>(16, 0.75f);
    }

    public void update(GroovyScript script) {
        if(script!=null) {
            this.cache.put(script.getKey(), script);
        }
    }

    public List<String> keys() {
        return new ArrayList<>(this.cache.keySet());
    }

    public GroovyScript getGroovyScript(String key) {
        return this.cache.get(key);
    }

    public String getScript(String key) {
        GroovyScript script = this.getGroovyScript(key);
        if (script != null) {
            return script.getScript();
        }
        return "";
    }

}
