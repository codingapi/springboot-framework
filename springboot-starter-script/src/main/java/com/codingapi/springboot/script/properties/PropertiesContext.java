package com.codingapi.springboot.script.properties;

import lombok.Getter;
import lombok.Setter;

/**
 *  参数上下文对象
 */
public class PropertiesContext {

    @Getter
    private final static PropertiesContext instance = new PropertiesContext();

    @Setter
    private GroovyScriptProperties properties;

    private PropertiesContext(){

    }

    public long getTempValidTime() {
        return properties.getTempValidTime();
    }

    public int getShellMaxCacheSize() {
        return properties.getShellMaxCacheSize();
    }
}
