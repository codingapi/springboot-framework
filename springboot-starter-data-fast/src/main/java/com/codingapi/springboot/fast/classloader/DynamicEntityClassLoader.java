package com.codingapi.springboot.fast.classloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DynamicEntityClassLoader extends ClassLoader {

    private final Map<String, Class<?>> dynamicClasses = new ConcurrentHashMap<>();

    public DynamicEntityClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void registerClass(String className, Class<?> clazz) {
        dynamicClasses.put(className, clazz);
    }

    public void registerClass(Class<?> clazz) {
        this.registerClass(clazz.getName(), clazz);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 首先检查已加载的动态类
        if (dynamicClasses.containsKey(name)) {
            return dynamicClasses.get(name);
        }
        // 委托给父类
        return super.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 首先检查是否已经加载
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                // 检查是否是动态类
                if (dynamicClasses.containsKey(name)) {
                    c = dynamicClasses.get(name);
                } else {
                    // 委托给父类
                    c = super.loadClass(name, resolve);
                }
            }

            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

}
