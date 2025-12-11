package com.codingapi.springboot.fast.classloader;

import lombok.Getter;

public class DynamicEntityClassLoaderContext {

    @Getter
    private final static DynamicEntityClassLoaderContext instance = new DynamicEntityClassLoaderContext();

    private final DynamicEntityClassLoader dynamicEntityClassLoader;

    private DynamicEntityClassLoaderContext(){
        dynamicEntityClassLoader = new DynamicEntityClassLoader(Thread.currentThread().getContextClassLoader() != null
                ? Thread.currentThread().getContextClassLoader()
                : getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(dynamicEntityClassLoader);
    }

    public void registerClass(String className, Class<?> clazz) {
        dynamicEntityClassLoader.registerClass(className, clazz);
    }

    public void registerClass(Class<?> clazz) {
        dynamicEntityClassLoader.registerClass(clazz);
    }

}
