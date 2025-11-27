package com.codingapi.springboot.framework.utils;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionUtils {

    public static String getJarVersion(Class<?> clazz) {
        try {
            String classPath = clazz.getResource(clazz.getSimpleName() + ".class").toString();
            if (!classPath.startsWith("jar")) {
                // 不是从 jar 启动的
                return "DEV";
            }

            String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
            try (InputStream inputStream = new java.net.URL(manifestPath).openStream()) {
                Manifest manifest = new Manifest(inputStream);
                Attributes attributes = manifest.getMainAttributes();
                return attributes.getValue("Implementation-Version");
            }
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }


    public static String getDriverVersion(){
        return VersionUtils.getJarVersion(VersionUtils.class);
    }


}
