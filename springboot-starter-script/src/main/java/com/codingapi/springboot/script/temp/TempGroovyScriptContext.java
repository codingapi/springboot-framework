package com.codingapi.springboot.script.temp;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.properties.PropertiesContext;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepositoryContext;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 临时脚本数据上下文管理对象
 */
public class TempGroovyScriptContext {

    @Getter
    private final static TempGroovyScriptContext instance = new TempGroovyScriptContext();

    private final Map<String, ClearJob> cache;

    private TempGroovyScriptContext() {
        this.cache = new HashMap<>();
    }

    private static class ClearJob {

        @Getter
        private final TempGroovyScript tempGroovyScript;

        private final Timer timer;

        public ClearJob(TempGroovyScript groovyScript) {
            this.tempGroovyScript = groovyScript;
            this.timer = new Timer();
            this.initTimer();
        }

        public GroovyScript getGroovyScript() {
            return this.tempGroovyScript.getGroovyScript();
        }

        private void initTimer() {
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TempGroovyScriptContext.getInstance().remove(getKey());
                }
            }, tempGroovyScript.getClearTime());
        }

        public String getKey() {
            return tempGroovyScript.getKey();
        }
    }

    /**
     * 更新脚本缓存
     *
     * @param script 脚本对象
     */
    public void save(GroovyScript script) {
        if (script != null) {
            long tempValidTime =  PropertiesContext.getInstance().getTempValidTime();
            this.cache.put(script.getKey(), new ClearJob(new TempGroovyScript(script, tempValidTime + System.currentTimeMillis())));
        }
    }

    /**
     * 加载到临时缓存
     */
    public void loadAll(List<TempGroovyScript> groovyScripts) {
        if (groovyScripts != null) {
            for (TempGroovyScript groovyScript : groovyScripts) {
                if (groovyScript.isExpired()) {
                    this.remove(groovyScript.getKey());
                } else {
                    this.cache.put(groovyScript.getKey(), new ClearJob(groovyScript));
                }
            }
        }
    }

    /**
     * 获取当前缓存的数据
     */
    public List<TempGroovyScript> findAll() {
        return this.cache.values().stream().map(ClearJob::getTempGroovyScript).collect(Collectors.toList());
    }


    /**
     * 删除脚本
     *
     * @param key 脚本key
     */
    public void remove(String key) {
        this.cache.remove(key);
        TempGroovyScriptRepositoryContext.getInstance().delete(key);
    }


    /**
     * 脚本总数量
     *
     * @return 总数量
     */
    public int count() {
        return this.cache.size();
    }

    /**
     * 获取脚本对象
     *
     * @param key 脚本key
     * @return 脚本对象
     */
    public GroovyScript getGroovyScript(String key) {
        ClearJob job = this.cache.get(key);
        if (job == null) {
            TempGroovyScript groovyScript = TempGroovyScriptRepositoryContext.getInstance().get(key);
            if (groovyScript != null) {
                this.cache.put(key, new ClearJob(groovyScript));
                return groovyScript.getGroovyScript();
            }
            return null;
        }
        return job.getGroovyScript();
    }


    /**
     * 清空脚本数据
     */
    public void clear() {
        this.cache.clear();
    }
}
