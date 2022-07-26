package com.codingapi.springboot.leaf.common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class PropertyFactory {

    private static final Properties prop = new Properties();
    static {
        try {
            prop.load(PropertyFactory.class.getClassLoader().getResourceAsStream("leaf.properties"));
        } catch (IOException e) {
            log.warn("Load Properties Ex", e);
        }
    }

    public static Properties getProperties() {
        return prop;
    }
}
