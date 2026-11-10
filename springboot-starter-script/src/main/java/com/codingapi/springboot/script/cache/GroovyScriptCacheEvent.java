package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.framework.event.ISyncEvent;
import lombok.Getter;

/**
 * 脚本缓存变更事件（同步事件）
 * <p>
 * 双通道设计：
 * 1. 跨进程：脚本在某个实例上保存或删除后经 {@link GroovyScriptCacheSync} SPI 广播，
 * 其他实例接收事件后失效本地缓存，下次读取自动回源仓储加载最新版本；
 * 2. 进程内：写路径同时经 EventPusher 推送本事件（无 Spring 上下文时为安全 no-op），
 * 业务代码可通过实现 IHandler&lt;GroovyScriptCacheEvent&gt; 监听脚本变更。
 */
@Getter
public class GroovyScriptCacheEvent implements ISyncEvent {

    public enum Type {
        /**
         * 脚本保存
         */
        SAVE,
        /**
         * 脚本删除
         */
        REMOVE
    }

    /**
     * 事件类型
     */
    private final Type type;

    /**
     * 脚本key
     */
    private final String key;

    /**
     * 事件来源实例标识（用于过滤本实例事件，避免回环）
     */
    private final String source;

    /**
     * 脚本更新时间
     */
    private final long updateTime;

    private GroovyScriptCacheEvent(Type type, String key, String source, long updateTime) {
        this.type = type;
        this.key = key;
        this.source = source;
        this.updateTime = updateTime;
    }

    /**
     * 构建脚本保存事件
     *
     * @param key        脚本key
     * @param source     来源实例标识
     * @param updateTime 更新时间
     * @return 缓存变更事件
     */
    public static GroovyScriptCacheEvent save(String key, String source, long updateTime) {
        return new GroovyScriptCacheEvent(Type.SAVE, key, source, updateTime);
    }

    /**
     * 构建脚本删除事件
     *
     * @param key    脚本key
     * @param source 来源实例标识
     * @return 缓存变更事件
     */
    public static GroovyScriptCacheEvent remove(String key, String source) {
        return new GroovyScriptCacheEvent(Type.REMOVE, key, source, 0);
    }

    /**
     * 判断事件是否来自指定实例
     *
     * @param source 实例标识
     * @return 是否来自指定实例
     */
    public boolean isFrom(String source) {
        return this.source != null && this.source.equals(source);
    }
}
