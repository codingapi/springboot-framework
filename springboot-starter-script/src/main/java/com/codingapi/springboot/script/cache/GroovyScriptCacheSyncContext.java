package com.codingapi.springboot.script.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * 脚本缓存同步上下文对象
 * <p>
 * 写路径通过 {@link #publish(GroovyScriptCacheEvent)} 发布脚本缓存变更事件；
 * 多实例部署时远端事件由同步实现回调 {@link #receive(GroovyScriptCacheEvent)} 失效本地缓存，
 * 避免其他实例永久持有旧脚本。
 */
public class GroovyScriptCacheSyncContext {

    @Getter
    private final static GroovyScriptCacheSyncContext instance = new GroovyScriptCacheSyncContext();

    /**
     * 当前实例标识，用于过滤本实例发布的事件回环
     */
    @Getter
    @Setter
    private String id = UUID.randomUUID().toString();

    @Setter
    private GroovyScriptCacheSync sync = new LocalGroovyScriptCacheSync();

    /**
     * 发布脚本缓存变更事件（写路径调用，由同步实现负责广播）
     *
     * @param event 缓存变更事件
     */
    public void publish(GroovyScriptCacheEvent event) {
        this.sync.publish(event);
    }

    /**
     * 接收远端实例广播的脚本缓存变更事件并失效本地缓存，
     * 本实例自身发布的事件将被忽略以避免回环
     *
     * @param event 缓存变更事件
     */
    public void receive(GroovyScriptCacheEvent event) {
        if (event == null || event.isFrom(this.id)) {
            return;
        }
        this.apply(event);
    }

    /**
     * 本地失效脚本缓存（收到事件后下次读取回源仓储）
     *
     * @param event 缓存变更事件
     */
    public void apply(GroovyScriptCacheEvent event) {
        if (event == null) {
            return;
        }
        GroovyScriptCacheContext.getInstance().remove(event.getKey());
    }
}
