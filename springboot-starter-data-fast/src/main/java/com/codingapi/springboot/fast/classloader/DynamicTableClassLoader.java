package com.codingapi.springboot.fast.classloader;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态表的 ClassLoader
 */
public class DynamicTableClassLoader {

    @Getter
    private final static DynamicTableClassLoader instance = new DynamicTableClassLoader();

    private final $DynamicTableClassLoader dynamicTableClassLoader;

    private DynamicTableClassLoader() {
        dynamicTableClassLoader = new $DynamicTableClassLoader(Thread.currentThread().getContextClassLoader() != null
                ? Thread.currentThread().getContextClassLoader()
                : getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(dynamicTableClassLoader);
    }

    /**
     * 注册类
     * @param className name
     * @param clazz class
     */
    public void registerClass(String className, Class<?> clazz) {
        dynamicTableClassLoader.registerClass(className, clazz);
    }

    /**
     * 注册类
     * @param clazz class
     */
    public void registerClass(Class<?> clazz) {
        dynamicTableClassLoader.registerClass(clazz);
    }

    /**
     * 获取类
     * @param name className
     * @return class
     */
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return dynamicTableClassLoader.findClass(name);
    }

    /**
     * 加载类
     * @param name className
     * @param resolve resolve
     * @return class
     */
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return dynamicTableClassLoader.loadClass(name, resolve);
    }

    /**
     * 清空动态类
     * @param name 类名称
     */
    public void cleanDynamicClass(String name) {
        dynamicTableClassLoader.cleanClass(name);
    }

    /**
     * 获取全部的动态类
     */
    public Collection<Class<?>> getDynamicClasses(){
        return dynamicTableClassLoader.getDynamicClasses();
    }

    /**
     * 获取全部动态类的名称
     */
    public Set<String> getDynamicClassNames(){
        return dynamicTableClassLoader.getDynamicClassNames();
    }


    /**
     * $DynamicTableClassLoader
     */
    private static class $DynamicTableClassLoader extends ClassLoader {

        private final Map<String, Class<?>> dynamicClasses = new ConcurrentHashMap<>();

        public $DynamicTableClassLoader(ClassLoader parent) {
            super(parent);
        }

        public void registerClass(String className, Class<?> clazz) {
            dynamicClasses.put(className, clazz);
        }

        public void registerClass(Class<?> clazz) {
            this.registerClass(clazz.getName(), clazz);
        }

        public void cleanClass(String className) {
            this.dynamicClasses.remove(className);
        }

        public Collection<Class<?>> getDynamicClasses(){
            return dynamicClasses.values();
        }

        public Set<String> getDynamicClassNames(){
            return dynamicClasses.keySet();
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


}
