package com.codingapi.springboot.script.cache;

/**
 * 脚本缓存同步 SPI
 * <p>
 * 单进程部署使用默认实现 {@link LocalGroovyScriptCacheSync}；
 * 多实例部署时实现该接口将缓存变更事件广播到其他实例
 * （如 Redis pub/sub、MQ 等），并在收到远端事件后调用
 * {@link GroovyScriptCacheSyncContext#receive(GroovyScriptCacheEvent)} 失效本地缓存。
 */
public interface GroovyScriptCacheSync {

    /**
     * 发布脚本缓存变更事件
     *
     * @param event 缓存变更事件
     */
    void publish(GroovyScriptCacheEvent event);
}
