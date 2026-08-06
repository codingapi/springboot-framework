package com.codingapi.springboot.script.temp;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.properties.PropertiesContext;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepositoryContext;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 临时脚本数据上下文管理对象
 */
public class TempGroovyScriptContext {

    @Getter
    private final static TempGroovyScriptContext instance = new TempGroovyScriptContext();

    /**
     * 共享的清理调度线程（daemon）
     * 所有临时脚本共用一个调度线程，避免每个脚本创建原生线程导致线程泄漏
     */
    private final static ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "temp-groovy-script-clear");
        thread.setDaemon(true);
        return thread;
    });

    private final Map<String, ClearJob> cache;

    private TempGroovyScriptContext() {
        this.cache = new ConcurrentHashMap<>();
    }

    private static class ClearJob {

        @Getter
        private final TempGroovyScript tempGroovyScript;

        private final ScheduledFuture<?> future;

        public ClearJob(TempGroovyScript groovyScript) {
            this.tempGroovyScript = groovyScript;
            // clearTime 为绝对到期时间戳，需换算为相对延迟
            long delay = groovyScript.getClearTime() - System.currentTimeMillis();
            this.future = SCHEDULER.schedule(this::expire, delay, TimeUnit.MILLISECONDS);
        }

        public GroovyScript getGroovyScript() {
            return this.tempGroovyScript.getGroovyScript();
        }

        public String getKey() {
            return tempGroovyScript.getKey();
        }

        /**
         * 到期清理
         * 仅当缓存中仍是当前任务时才删除，避免过期任务误删已被刷新的脚本
         */
        private void expire() {
            TempGroovyScriptContext.getInstance().removeIfCurrent(getKey(), this);
        }

        /**
         * 取消定时清理任务
         */
        private void cancel() {
            this.future.cancel(false);
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
            this.put(script.getKey(), new ClearJob(new TempGroovyScript(script, tempValidTime + System.currentTimeMillis())));
        }
    }

    /**
     * 写入缓存，覆盖时取消旧任务的定时清理
     */
    private void put(String key, ClearJob job) {
        ClearJob previous = this.cache.put(key, job);
        if (previous != null) {
            previous.cancel();
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
                    this.put(groovyScript.getKey(), new ClearJob(groovyScript));
                }
            }
        }
    }

    /**
     * 获取当前缓存的数据
     */
    public List<TempGroovyScript> findAll() {
        return this.cache.values().stream().map(ClearJob::getTempGroovyScript).toList();
    }


    /**
     * 删除脚本
     *
     * @param key 脚本key
     */
    public void remove(String key) {
        ClearJob job = this.cache.remove(key);
        if (job != null) {
            job.cancel();
        }
        TempGroovyScriptRepositoryContext.getInstance().delete(key);
    }

    /**
     * 仅当缓存中的任务仍为当前任务时删除，防止过期任务误删已被刷新的脚本。
     * 使用 ConcurrentHashMap.remove(key, value) 原子判断 + 删除，避免 check-then-act 竞态
     */
    private void removeIfCurrent(String key, ClearJob job) {
        if (this.cache.remove(key, job)) {
            job.cancel();
            TempGroovyScriptRepositoryContext.getInstance().delete(key);
        }
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
                if (groovyScript.isExpired()) {
                    this.remove(key);
                    return null;
                }
                this.put(key, new ClearJob(groovyScript));
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
        for (ClearJob job : this.cache.values()) {
            job.cancel();
        }
        this.cache.clear();
    }
}
