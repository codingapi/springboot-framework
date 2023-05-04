package com.codingapi.springboot.framework.boot;

import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DynamicApplication {

    private final static DynamicApplication instance = new DynamicApplication();

    public static DynamicApplication getInstance() {
        return instance;
    }

    private DynamicApplication() {
    }

    private ConfigurableApplicationContext context;
    private String[] runArgs;
    private Class<?> applicationClass;

    @Setter
    private String jarsFolder = "./jars";


    public void run0(Class<?> applicationClass, String[] args) {
        this.runArgs = args;
        this.applicationClass = applicationClass;
        context = SpringApplication.run(applicationClass, args);
    }


    public static void run(Class<?> applicationClass, String[] args) {
        DynamicApplication.getInstance().run0(applicationClass, args);
    }

    public static void restart() {
        DynamicApplication.getInstance().restart0();
    }

    public void restart0() {
        Thread thread = new Thread(() -> {
            context.close();
            try {
                this.addDynamicJars();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.run0(applicationClass, runArgs);
        });
        thread.setDaemon(false);
        thread.start();
    }

    private void addDynamicJars() throws IOException {
        Path libsPath = Paths.get(jarsFolder);
        if (Files.exists(libsPath) && Files.isDirectory(libsPath)) {
            System.out.println("Start Load Dynamic Jars:");
            List<URL> jarUrls = new ArrayList<>();
            Files.list(libsPath)
                    .filter(file -> file.toString().endsWith(".jar"))
                    .forEach(file -> {
                        try {
                            URL url = file.toUri().toURL();
                            jarUrls.add(url);
                            System.out.println(url);
                        } catch (Exception ignored) {}
                    });
            URL[] urls = jarUrls.toArray(new URL[0]);
            URLClassLoader urlClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
            Thread.currentThread().setContextClassLoader(urlClassLoader);
        }
    }
}
