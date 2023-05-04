package com.codingapi.springboot.framework.boot;

import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
        DynamicApplication.getInstance().addDynamicJars();
        DynamicApplication.getInstance().run0(applicationClass, args);
    }

    public static void restart() {
        DynamicApplication.getInstance().restart0();
    }

    public void restart0() {
        Thread thread = new Thread(() -> {
            context.close();
            this.addDynamicJars();
            this.run0(applicationClass, runArgs);
        });
        thread.setDaemon(false);
        thread.start();
    }

    private void addDynamicJars(){
        Path libsPath = Paths.get(jarsFolder);
        if (Files.exists(libsPath) && Files.isDirectory(libsPath)) {
            System.out.println("Start Load Dynamic Jars:");
            List<URL> jarUrls = new ArrayList<>();
            try(Stream<Path> stream = Files.list(libsPath)){
                stream
                        .filter(file -> file.toString().endsWith(".jar"))
                        .map(Path::toUri)
                        .forEach(uri -> {
                            try {
                                jarUrls.add(uri.toURL());
                                System.out.println(uri);
                            } catch (Exception ignored) {}
                        });
            }catch (Exception ignored){}
            URL[] urls = jarUrls.toArray(new URL[0]);
            URLClassLoader urlClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
            Thread.currentThread().setContextClassLoader(urlClassLoader);
        }
    }
}
