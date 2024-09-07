package com.codingapi.springboot.framework.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassLoaderUtils {

    public static URLClassLoader createClassLoader(String jarPath) throws MalformedURLException {
        File file = new File(jarPath);
        if(!file.exists()){
            throw new RuntimeException("jar file not found:"+jarPath);
        }
        URL[] urls = new URL[]{file.toURI().toURL()};
        return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
    }

    public static List<String> findAllClasses(URLClassLoader classLoader) throws URISyntaxException, IOException {
        List<String> classNames = new ArrayList<>();
        URL[] urls = classLoader.getURLs();
        for (URL url : urls) {
            if (url.getProtocol().equals("file")) {
                File file = new File(url.toURI());
                if (file.isDirectory()) {
                    classNames.addAll(findClassesInDirectory(file, ""));
                } else if (file.getName().endsWith(".jar")) {
                    classNames.addAll(findClassesInJar(new JarFile(file)));
                }
            }
        }
        return classNames;
    }

    public static List<Class<?>> findJarClasses(String jarPath) throws IOException, URISyntaxException {
        URLClassLoader classLoader = createClassLoader(jarPath);
        List<String> classList = ClassLoaderUtils.findAllClasses(classLoader);
        List<Class<?>> classes = new ArrayList<>();
        for(String className:classList){
            try {
                Class<?> driverClass = classLoader.loadClass(className);
                classes.add(driverClass);
            }catch (NoClassDefFoundError | ClassNotFoundException ignored){}
        }
        return classes;
    }

    public static List<Class<?>> findJarClass(String jarPath,Class<?> clazz) throws IOException, URISyntaxException {
        URLClassLoader classLoader = createClassLoader(jarPath);
        List<String> classList = ClassLoaderUtils.findAllClasses(classLoader);
        List<Class<?>> classes = new ArrayList<>();
        for(String className:classList){
            try {
                Class<?> driverClass = classLoader.loadClass(className);
                if(clazz.isAssignableFrom(driverClass)){
                   classes.add(driverClass);
                }
            }catch (NoClassDefFoundError | ClassNotFoundException ignored){}
        }
        return classes;
    }

    private static List<String> findClassesInDirectory(File directory, String packageName) {
        List<String> classNames = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classNames.addAll(findClassesInDirectory(file, packageName + file.getName() + "."));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + file.getName().replace(".class", "");
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }

    private static List<String> findClassesInJar(JarFile jarFile) {
        List<String> classNames = new ArrayList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(".class")) {
                String className = name.replace('/', '.').replace(".class", "");
                classNames.add(className);
            }
        }
        return classNames;
    }
}
