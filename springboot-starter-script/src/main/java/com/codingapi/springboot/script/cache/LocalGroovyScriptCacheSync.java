package com.codingapi.springboot.script.cache;

/**
 * 默认脚本缓存同步实现：单进程场景，事件直接在本进程内生效
 */
public class LocalGroovyScriptCacheSync implements GroovyScriptCacheSync {

    @Override
    public void publish(GroovyScriptCacheEvent event) {
        GroovyScriptCacheSyncContext.getInstance().apply(event);
    }
}
