package com.codingapi.springboot.script.temp;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.properties.GroovyScriptProperties;
import com.codingapi.springboot.script.properties.PropertiesContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 回归测试：临时脚本不得为每个脚本创建原生线程（OOM 根因），
 * 且脚本到期后应被自动清理。
 */
class TempGroovyScriptContextTest {

    private TempGroovyScriptContext context;

    @BeforeEach
    void setUp() {
        context = TempGroovyScriptContext.getInstance();
        // 清理上一用例残留的定时任务
        context.clear();
        // 预热共享调度线程，避免首个任务创建线程影响基线统计
        context.save(script("__warmup__"));
        context.remove("__warmup__");
    }

    @AfterEach
    void tearDown() {
        // 清理定时任务并恢复默认配置，避免泄漏到同 JVM 的其他测试
        context.clear();
        PropertiesContext.getInstance().setProperties(new GroovyScriptProperties());
    }

    private GroovyScript script(String key) {
        return GroovyScript.builder(key).script("return 1;").build();
    }

    @Test
    void saveManyScriptsShouldNotCreateThreads() {
        int threadsBefore = Thread.getAllStackTraces().size();

        for (int i = 0; i < 200; i++) {
            context.save(script("key-" + i));
        }

        assertEquals(200, context.count());
        assertEquals(threadsBefore, Thread.getAllStackTraces().size(),
                "注册 200 个临时脚本不应创建任何新线程");
    }

    @Test
    void overwriteRemoveClearShouldNotCreateThreads() {
        int threadsBefore = Thread.getAllStackTraces().size();

        for (int i = 0; i < 100; i++) {
            context.save(script("key-" + i));
            context.save(script("key-" + i)); // 覆盖刷新
        }
        for (int i = 0; i < 100; i++) {
            context.remove("key-" + i);
        }
        context.clear();

        assertEquals(0, context.count());
        assertEquals(threadsBefore, Thread.getAllStackTraces().size(),
                "覆盖/删除/清空不应创建新线程");
    }

    @Test
    void expiredScriptShouldBeRemovedAutomatically() throws InterruptedException {
        GroovyScriptProperties properties = new GroovyScriptProperties();
        properties.setTempValidTime(300);
        PropertiesContext.getInstance().setProperties(properties);

        context.save(script("expire-key"));
        assertEquals(1, context.count());

        Thread.sleep(1500);
        assertEquals(0, context.count(), "脚本到期后应被自动清理");
    }
}