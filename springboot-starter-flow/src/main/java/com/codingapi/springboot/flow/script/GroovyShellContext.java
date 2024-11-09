package com.codingapi.springboot.flow.script;

import com.codingapi.springboot.flow.utils.Sha256Utils;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroovyShellContext {

    @Getter
    private final static GroovyShellContext instance = new GroovyShellContext();

    private final static GroovyShell groovyShell = new GroovyShell();

    private final Map<String, ShellScript> cache = new HashMap<>();

    private final static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    // 缓存最大值
    private final static int MAX_CACHE_SIZE = 10000;


    private GroovyShellContext() {
    }

    public ShellScript parse(String script) {
        String hash = Sha256Utils.generateSHA256(script);
        if (cache.containsKey(hash)) {
            return cache.get(hash);
        } else {
            if (cache.size() > MAX_CACHE_SIZE) {
                cache.clear();
            }
            ShellScript shellScript = new ShellScript(script);
            threadPool.submit(shellScript);
            cache.put(hash, shellScript);
            return shellScript;
        }
    }


    public int size() {
        return cache.size();
    }

    public static class ShellScript implements Runnable {

        @Getter
        private final String script;

        private Script runtime;

        public ShellScript(String script) {
            this.script = script;
        }

        public Object invokeMethod(String run, Object params) {
            synchronized (ShellScript.this) {
                if (runtime == null) {
                    try {
                        ShellScript.this.wait(1000);
                    } catch (InterruptedException ignored) {
                    }
                    if (runtime == null) {
                        runtime = groovyShell.parse(script);
                    }
                }
                return runtime.invokeMethod(run, params);
            }
        }

        @Override
        public void run() {
            runtime = groovyShell.parse(script);
            synchronized (ShellScript.this) {
                this.notifyAll();
            }
        }
    }


}
