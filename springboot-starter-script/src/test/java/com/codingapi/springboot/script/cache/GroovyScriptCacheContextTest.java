package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GroovyScriptCacheContext 单元测试
 * 覆盖 LRU 缓存的存取、回源（临时缓存/仓储）、批量加载、编译与淘汰逻辑
 */
class GroovyScriptCacheContextTest {

    private final GroovyScriptCacheContext context = GroovyScriptCacheContext.getInstance();

    private GroovyScript script(String key) {
        return GroovyScript.builder(key)
                .script("return 1;")
                .description("desc-" + key)
                .method("run")
                .returnType(Integer.class)
                .build();
    }

    @BeforeEach
    void setUp() {
        context.clear();
        TempGroovyScriptContext.getInstance().clear();
    }

    @AfterEach
    void tearDown() {
        context.clear();
        TempGroovyScriptContext.getInstance().clear();
        // 清理回源测试写入仓储的数据
        GroovyScriptRepositoryContext.getInstance().delete("cache-repo-key");
        GroovyScriptRepositoryContext.getInstance().delete("cache-save-key");
    }

    @Test
    void saveAndCacheShouldPutScriptIntoCache() {
        GroovyScript s1 = script("cache-key-1");
        GroovyScript s2 = script("cache-key-2");

        context.save(s1);
        context.cache(s2);
        // null 入参直接忽略
        context.save(null);
        context.cache(null);

        assertEquals(2, context.count());
        List<String> keys = context.keys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("cache-key-1"));
        assertTrue(keys.contains("cache-key-2"));
        assertSame(s1, context.getGroovyScript("cache-key-1"));
    }

    @Test
    void removeShouldDeleteExistingKeyAndIgnoreMissingKey() {
        context.save(script("cache-remove-key"));
        assertEquals(1, context.count());

        context.remove("cache-remove-key");
        assertEquals(0, context.count());

        // 删除不存在的key不抛异常
        context.remove("cache-remove-key-missing");
        assertEquals(0, context.count());
    }

    @Test
    void getGroovyScriptShouldFallbackToTempContextWithoutCaching() {
        GroovyScript tempScript = script("cache-temp-key");
        TempGroovyScriptContext.getInstance().save(tempScript);

        GroovyScript result = context.getGroovyScript("cache-temp-key");
        assertSame(tempScript, result);
        // 临时数据不回写到缓存
        assertEquals(0, context.count());
    }

    @Test
    void getGroovyScriptShouldFallbackToRepositoryAndCacheResult() {
        GroovyScript repoScript = script("cache-repo-key");
        GroovyScriptRepositoryContext.getInstance().save(repoScript);

        GroovyScript result = context.getGroovyScript("cache-repo-key");
        assertSame(repoScript, result);
        // 仓储数据会回写到缓存
        assertEquals(1, context.count());

        // 再次获取直接命中缓存
        assertSame(repoScript, context.getGroovyScript("cache-repo-key"));
    }

    @Test
    void getGroovyScriptShouldReturnNullWhenMissingEverywhere() {
        assertNull(context.getGroovyScript("cache-no-such-key"));
    }

    @Test
    void getGroovyMetadataShouldReturnMetadataOrNull() {
        context.save(script("cache-meta-key"));
        GroovyMetadata metadata = context.getGroovyMetadata("cache-meta-key");
        assertNotNull(metadata);
        assertEquals("run", metadata.getMainMethod());
        assertEquals("Integer", metadata.getReturnType());

        assertNull(context.getGroovyMetadata("cache-no-such-key"));
    }

    @Test
    void getScriptShouldReturnContentOrEmptyString() {
        context.save(script("cache-script-key"));
        assertEquals("return 1;", context.getScript("cache-script-key"));
        assertEquals("", context.getScript("cache-no-such-key"));
    }

    @Test
    void setBatchCacheShouldIgnoreNullListAndNullElements() {
        context.setBatchCache(null);
        assertEquals(0, context.count());

        List<GroovyScript> list = new ArrayList<>();
        list.add(script("cache-batch-1"));
        list.add(null);
        list.add(script("cache-batch-2"));
        context.setBatchCache(list);

        assertEquals(2, context.count());
        assertNotNull(context.getGroovyScript("cache-batch-1"));
        assertNotNull(context.getGroovyScript("cache-batch-2"));
    }

    @Test
    void compileAllShouldCompileEveryCachedScript() {
        context.save(script("cache-compile-1"));
        context.save(script("cache-compile-2"));

        // 非缓存模式编译全部脚本
        context.compileAll(false);
        assertEquals(2, context.count());
    }

    @Test
    void lruCacheShouldEvictEldestEntryWhenOverMaxSize() {
        // MAX_CACHE_SIZE = 10 * 1024，写入超出一条即触发 LRU 淘汰
        int total = 10 * 1024 + 1;
        for (int i = 0; i < total; i++) {
            context.save(GroovyScript.builder("cache-lru-" + i).script("return 1;").build());
        }

        assertEquals(10 * 1024, context.count());
        // 最早写入的被淘汰（缓存/临时/仓储均无，返回 null）
        assertNull(context.getGroovyScript("cache-lru-0"));
        // 其后写入的仍然存在
        assertNotNull(context.getGroovyScript("cache-lru-1"));
        assertEquals(10 * 1024, context.count());
    }

    @Test
    void clearShouldRemoveAllEntries() {
        context.save(script("cache-clear-1"));
        context.save(script("cache-clear-2"));
        assertEquals(2, context.count());

        context.clear();
        assertEquals(0, context.count());
        assertTrue(context.keys().isEmpty());
    }
}
