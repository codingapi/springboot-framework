package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.GroovyScript;
import lombok.Getter;
import lombok.Setter;

/**
 *  GroovyScriptRepository 上下文对象
 */
public class GroovyScriptRepositoryContext {

    @Getter
    private final static GroovyScriptRepositoryContext instance = new GroovyScriptRepositoryContext();

    private GroovyScriptRepositoryContext() {

    }

    @Setter
    private GroovyScriptRepository groovyScriptRepository = new GroovyScriptRepository() {
        @Override
        public void save(GroovyScript groovyScript) {

        }

        @Override
        public void delete(GroovyScript groovyScript) {

        }

        @Override
        public GroovyScript get(String key) {
            return null;
        }
    };

    public void save(GroovyScript groovyScript) {
        this.groovyScriptRepository.save(groovyScript);
    }


    public void delete(GroovyScript groovyScript) {
        this.groovyScriptRepository.delete(groovyScript);
    }

    public GroovyScript get(String key) {
        return  this.groovyScriptRepository.get(key);
    }
}
