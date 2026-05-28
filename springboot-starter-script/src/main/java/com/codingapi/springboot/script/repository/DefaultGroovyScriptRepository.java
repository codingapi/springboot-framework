package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.GroovyScript;

import java.util.HashMap;
import java.util.Map;

public class DefaultGroovyScriptRepository implements GroovyScriptRepository {

    private final Map<String, GroovyScript> data;

    public DefaultGroovyScriptRepository() {
        this.data = new HashMap<>();
        GroovyScriptRepositoryContext.getInstance().setGroovyScriptRepository(this);
    }

    @Override
    public void save(GroovyScript groovyScript) {
        this.data.put(groovyScript.getKey(), groovyScript);
    }

    @Override
    public void delete(GroovyScript groovyScript) {
        this.data.remove(groovyScript.getKey());
    }

    @Override
    public GroovyScript get(String key) {
        return this.data.get(key);
    }
}
