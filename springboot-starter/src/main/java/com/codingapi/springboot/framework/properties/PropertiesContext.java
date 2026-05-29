package com.codingapi.springboot.framework.properties;

import lombok.Getter;
import lombok.Setter;

public class PropertiesContext {

    @Getter
    private final static PropertiesContext instance = new PropertiesContext();

    private PropertiesContext(){

    }

    @Setter
    private FrameworkProperties properties;

    public int getHandlerThreadPoolSize() {
        return properties.getHandlerThreadPoolSize();
    }

}
