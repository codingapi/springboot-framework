package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * GroovyScriptCacheSyncContext 单元测试
 * 覆盖：脚本保存/删除后的事件发布、远端事件触发本地缓存失效、
 * 本实例事件回环过滤、缓存策略可插拔替换（issue #214）
 */
class GroovyScriptCacheSyncContextTest {

    private static final String LOCAL_ID = "local-instance";
    private static final String REMOTE_ID = "remote-instance";

    private final GroovyScriptCacheContext cacheContext = GroovyScriptCacheContext.getInstance();
    private final GroovyScriptCacheSyncContext syncContext = GroovyScriptCacheSyncContext.getInstance();

    private RecordingSync recordingSync;

    /**
     * 记录型同步实现，模拟跨实例广播通道
     */
    static class RecordingSync implements GroovyScriptCacheSync {
        final List<GroovyScriptCacheEvent> events = new ArrayList<>();

        @Override
        public void publish(GroovyScriptCacheEvent event) {
            this.events.add(event);
        }
    }

    /**
     * 基于 HashMap 的简单缓存策略，验证缓存容器可替换
     */
    static class MapGroovyScriptCache implements GroovyScriptCache {
        private final Map<String, GroovyScript> cache = new HashMap<>();

        @Override
        public void save(GroovyScript script) {
            if (script != null) {
                cache.put(script.getKey(), script);
            }
        }

        @Override
        public GroovyScript get(String key) {
            return cache.get(key);
        }

        @Override
        public void remove(String key) {
            cache.remove(key);
        }

        @Override
        public List<String> keys() {
            return new ArrayList<>(cache.keySet());
        }

        @Override
        public int count() {
            return cache.size();
        }

        @Override
        public void clear() {
            cache.clear();
        }
    }

    private GroovyScript script(String key, String content) {
        return GroovyScript.builder(key)
                .script(content)
                .method("run")
                .returnType(Integer.class)
                .build();
    }

    @BeforeEach
    void setUp() {
        cacheContext.clear();
        TempGroovyScriptContext.getInstance().clear();
        recordingSync = new RecordingSync();
        syncContext.setSync(recordingSync);
        syncContext.setId(LOCAL_ID);
        cacheContext.setCache(new DefaultGroovyScriptCache());
    }

    @AfterEach
    void tearDown() {
        // 恢复默认实现，避免影响其他测试
        syncContext.setSync(new LocalGroovyScriptCacheSync());
        cacheContext.setCache(new DefaultGroovyScriptCache());
        cacheContext.clear();
        TempGroovyScriptContext.getInstance().clear();
        GroovyScriptRepositoryContext.getInstance().delete("sync-key");
    }

    @Test
    void saveShouldPublishSaveEvent() {
        GroovyScript s = script("sync-key", "return 1;");
        GroovyScriptRepositoryContext.getInstance().save(s);
        cacheContext.getGroovyScript("sync-key");

        s.setScript("return 2;");
        s.save();

        assertEquals(1, recordingSync.events.size());
        GroovyScriptCacheEvent event = recordingSync.events.get(0);
        assertEquals(GroovyScriptCacheEvent.Type.SAVE, event.getType());
        assertEquals("sync-key", event.getKey());
        assertEquals(LOCAL_ID, event.getSource());
        assertEquals(s.getUpdateTime(), event.getUpdateTime());
    }

    @Test
    void removeShouldPublishRemoveEvent() {
        GroovyScript s = script("sync-key", "return 1;");
        GroovyScriptRepositoryContext.getInstance().save(s);
        cacheContext.getGroovyScript("sync-key");

        s.remove();

        assertEquals(1, recordingSync.events.size());
        GroovyScriptCacheEvent event = recordingSync.events.get(0);
        assertEquals(GroovyScriptCacheEvent.Type.REMOVE, event.getType());
        assertEquals("sync-key", event.getKey());
        assertEquals(LOCAL_ID, event.getSource());
    }

    @Test
    void defaultLocalSyncShouldEvictCacheOnSaveSoNextReadReloadsFromRepository() {
        // 恢复默认单进程同步实现
        syncContext.setSync(new LocalGroovyScriptCacheSync());

        GroovyScript s = script("sync-key", "return 1;");
        GroovyScriptRepositoryContext.getInstance().save(s);
        GroovyScript cached = cacheContext.getGroovyScript("sync-key");
        assertSame(cached, cacheContext.getGroovyScript("sync-key"));

        s.setScript("return 2;");
        s.save();

        // 本地缓存已失效，重新回源仓储加载新版本内容
        assertEquals(0, cacheContext.count());
        GroovyScript reloaded = cacheContext.getGroovyScript("sync-key");
        assertEquals("return 2;", reloaded.getScript());
    }

    @Test
    void receiveShouldEvictLocalCacheAndReloadNewVersionFromRepository() {
        // 模拟远端实例保存了新版本脚本，本实例收到广播事件
        GroovyScript s = script("sync-key", "return 1;");
        GroovyScriptRepositoryContext.getInstance().save(s);
        GroovyScript cached = cacheContext.getGroovyScript("sync-key");
        assertSame(cached, cacheContext.getGroovyScript("sync-key"));

        GroovyScript newVersion = script("sync-key", "return 2;");
        GroovyScriptRepositoryContext.getInstance().save(newVersion);
        syncContext.receive(GroovyScriptCacheEvent.save("sync-key", REMOTE_ID, System.currentTimeMillis()));

        assertEquals(0, cacheContext.count());
        GroovyScript reloaded = cacheContext.getGroovyScript("sync-key");
        assertNotSame(cached, reloaded);
        assertEquals("return 2;", reloaded.getScript());
    }

    @Test
    void receiveShouldIgnoreEventFromSelf() {
        GroovyScript s = script("sync-key", "return 1;");
        cacheContext.save(s);
        assertEquals(1, cacheContext.count());

        syncContext.receive(GroovyScriptCacheEvent.save("sync-key", LOCAL_ID, System.currentTimeMillis()));

        assertEquals(1, cacheContext.count());
        assertSame(s, cacheContext.getGroovyScript("sync-key"));
    }

    @Test
    void receiveShouldEvictLocalCacheAndReturnNullAfterRemoteRemove() {
        GroovyScript s = script("sync-key", "return 1;");
        GroovyScriptRepositoryContext.getInstance().save(s);
        cacheContext.getGroovyScript("sync-key");
        assertEquals(1, cacheContext.count());

        // 模拟远端实例删除脚本：远端先删仓储，再广播删除事件
        GroovyScriptRepositoryContext.getInstance().delete("sync-key");
        syncContext.receive(GroovyScriptCacheEvent.remove("sync-key", REMOTE_ID));

        assertEquals(0, cacheContext.count());
        assertNull(cacheContext.getGroovyScript("sync-key"));
    }

    @Test
    void receiveShouldIgnoreNullEvent() {
        syncContext.receive(null);
        assertEquals(0, cacheContext.count());
    }

    @Test
    void publishShouldDelegateToConfiguredSync() {
        syncContext.publish(GroovyScriptCacheEvent.remove("sync-key", LOCAL_ID));

        assertEquals(1, recordingSync.events.size());
        assertEquals(GroovyScriptCacheEvent.Type.REMOVE, recordingSync.events.get(0).getType());
    }

    @Test
    void setCacheShouldReplaceCacheStrategy() {
        MapGroovyScriptCache customCache = new MapGroovyScriptCache();
        cacheContext.setCache(customCache);

        GroovyScript s = script("sync-key", "return 1;");
        cacheContext.save(s);

        assertEquals(1, customCache.count());
        assertSame(s, customCache.get("sync-key"));
        assertSame(s, cacheContext.getGroovyScript("sync-key"));

        // null 回退为默认 LRU 实现
        cacheContext.setCache(null);
        assertEquals(0, cacheContext.count());
    }
}
