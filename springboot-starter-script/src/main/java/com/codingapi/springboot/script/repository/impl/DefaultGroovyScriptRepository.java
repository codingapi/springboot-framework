package com.codingapi.springboot.script.repository.impl;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.repository.GroovyScriptRepository;

import java.util.HashMap;
import java.util.Map;

public class DefaultGroovyScriptRepository implements GroovyScriptRepository {

    private final Map<String, GroovyScript> data;

    public DefaultGroovyScriptRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(GroovyScript groovyScript) {
        this.data.put(groovyScript.getKey(), groovyScript);
    }

    @Override
    public void delete(String key) {
        this.data.remove(key);
    }

    @Override
    public GroovyScript get(String key) {
        return this.data.get(key);
    }
}
