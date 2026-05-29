package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.repository.impl.DefaultGroovyScriptRepository;
import lombok.Getter;
import lombok.Setter;

/**
 *  GroovyScriptRepository 上下文对象
 */
public class GroovyScriptRepositoryContext {

    @Getter
    private final static GroovyScriptRepositoryContext instance = new GroovyScriptRepositoryContext();

    private GroovyScriptRepositoryContext() {
        this.groovyScriptRepository = new DefaultGroovyScriptRepository();
    }

    @Setter
    private GroovyScriptRepository groovyScriptRepository;

    public void save(GroovyScript groovyScript) {
        this.groovyScriptRepository.save(groovyScript);
    }


    public void delete(String key) {
        this.groovyScriptRepository.delete(key);
    }

    public GroovyScript get(String key) {
        return  this.groovyScriptRepository.get(key);
    }
}
