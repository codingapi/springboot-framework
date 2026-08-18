package com.codingapi.springboot.script.temp;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepositoryContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * TempGroovyScriptContext 补充测试
 * 覆盖 getGroovyScript 回源仓储、loadAll 批量加载、findAll 查询
 */
class TempGroovyScriptContextLoadTest {

    private final TempGroovyScriptContext context = TempGroovyScriptContext.getInstance();

    private GroovyScript script(String key) {
        return GroovyScript.builder(key).script("return 1;").build();
    }

    @BeforeEach
    void setUp() {
        context.clear();
    }

    @AfterEach
    void tearDown() {
        context.clear();
        // 清理直接写入仓储的数据
        TempGroovyScriptRepositoryContext.getInstance().delete("temp-repo-valid");
        TempGroovyScriptRepositoryContext.getInstance().delete("temp-repo-expired");
        TempGroovyScriptRepositoryContext.getInstance().delete("temp-load-expired");
    }

    @Test
    void getGroovyScriptShouldReturnNullWhenAbsent() {
        assertNull(context.getGroovyScript("temp-no-such-key"));
    }

    @Test
    void getGroovyScriptShouldReturnSavedScript() {
        GroovyScript groovyScript = script("temp-get-key");
        context.save(groovyScript);

        assertSame(groovyScript, context.getGroovyScript("temp-get-key"));
        assertEquals(1, context.count());
    }

    @Test
    void getGroovyScriptShouldReloadValidScriptFromRepository() {
        TempGroovyScript repositoryScript =
                new TempGroovyScript(script("temp-repo-valid"), System.currentTimeMillis() + 60000);
        TempGroovyScriptRepositoryContext.getInstance().save(repositoryScript);

        GroovyScript result = context.getGroovyScript("temp-repo-valid");
        assertNotNull(result);
        assertEquals("temp-repo-valid", result.getKey());
        // 重新加载后进入内存缓存
        assertEquals(1, context.count());
    }

    @Test
    void getGroovyScriptShouldRemoveExpiredScriptFromRepository() {
        TempGroovyScript expiredScript =
                new TempGroovyScript(script("temp-repo-expired"), System.currentTimeMillis() - 1);
        TempGroovyScriptRepositoryContext.getInstance().save(expiredScript);

        assertNull(context.getGroovyScript("temp-repo-expired"));
        assertEquals(0, context.count());
        // 过期数据同时从仓储清理
        assertNull(TempGroovyScriptRepositoryContext.getInstance().get("temp-repo-expired"));
    }

    @Test
    void loadAllShouldSkipExpiredAndKeepValidScripts() {
        // 预写一条过期数据到仓储，loadAll 时应被删除
        TempGroovyScript expiredInRepo =
                new TempGroovyScript(script("temp-load-expired"), System.currentTimeMillis() - 1);
        TempGroovyScriptRepositoryContext.getInstance().save(expiredInRepo);

        List<TempGroovyScript> list = new ArrayList<>();
        list.add(new TempGroovyScript(script("temp-load-1"), System.currentTimeMillis() + 60000));
        list.add(new TempGroovyScript(script("temp-load-2"), System.currentTimeMillis() + 60000));
        list.add(new TempGroovyScript(script("temp-load-expired"), System.currentTimeMillis() - 1));

        context.loadAll(list);

        assertEquals(2, context.count());
        assertNotNull(context.getGroovyScript("temp-load-1"));
        assertNotNull(context.getGroovyScript("temp-load-2"));
        assertNull(TempGroovyScriptRepositoryContext.getInstance().get("temp-load-expired"));
    }

    @Test
    void loadAllWithNullListShouldDoNothing() {
        context.loadAll(null);
        assertEquals(0, context.count());
    }

    @Test
    void findAllShouldReturnCurrentTempScripts() {
        context.save(script("temp-find-1"));
        context.save(script("temp-find-2"));

        List<TempGroovyScript> all = context.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void overwriteSameKeyShouldKeepSingleEntry() {
        context.save(script("temp-overwrite"));
        context.save(script("temp-overwrite"));
        assertEquals(1, context.count());

        context.remove("temp-overwrite");
        assertEquals(0, context.count());
    }
}
