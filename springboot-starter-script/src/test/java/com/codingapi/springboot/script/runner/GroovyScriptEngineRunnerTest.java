package com.codingapi.springboot.script.runner;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.repository.TempGroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * GroovyScriptEngineRunner 单元测试
 * 覆盖启动时从仓储分页加载临时脚本、销毁时持久化临时脚本
 */
class GroovyScriptEngineRunnerTest {

    private final GroovyScriptEngineRunner runner = new GroovyScriptEngineRunner();

    private GroovyScript script(String key) {
        return GroovyScript.builder(key).script("return 1;").build();
    }

    @BeforeEach
    void setUp() {
        TempGroovyScriptContext.getInstance().clear();
    }

    @AfterEach
    void tearDown() {
        TempGroovyScriptContext.getInstance().clear();
        for (int i = 0; i < 150; i++) {
            TempGroovyScriptRepositoryContext.getInstance().delete("runner-key-" + i);
        }
        TempGroovyScriptRepositoryContext.getInstance().delete("destroy-key-1");
        TempGroovyScriptRepositoryContext.getInstance().delete("destroy-key-2");
    }

    @Test
    void afterPropertiesSetShouldLoadTempScriptsFromRepository() throws Exception {
        // 写入 150 条数据（超过单页 100 条，触发分页加载逻辑）
        // clearTime 递增，保证按 clearTime 排序后 runner-key-0 必然在首页
        long clearTime = System.currentTimeMillis() + 60000;
        for (int i = 0; i < 150; i++) {
            TempGroovyScriptRepositoryContext.getInstance()
                    .save(new TempGroovyScript(script("runner-key-" + i), clearTime + i));
        }

        runner.afterPropertiesSet();

        // 注意：源码仅在 page.hasNext() 时加载当前页，最后一页（50 条）不会加载，
        // 疑似 bug（详见测试报告），此处按实际行为断言
        assertEquals(100, TempGroovyScriptContext.getInstance().count());
        assertNotNull(TempGroovyScriptContext.getInstance().getGroovyScript("runner-key-0"));
    }

    @Test
    void afterPropertiesSetWithEmptyRepositoryShouldLoadNothing() throws Exception {
        runner.afterPropertiesSet();
        assertEquals(0, TempGroovyScriptContext.getInstance().count());
    }

    @Test
    void destroyShouldSaveTempScriptsToRepository() throws Exception {
        TempGroovyScriptContext.getInstance().save(script("destroy-key-1"));
        TempGroovyScriptContext.getInstance().save(script("destroy-key-2"));

        runner.destroy();

        assertNotNull(TempGroovyScriptRepositoryContext.getInstance().get("destroy-key-1"));
        assertNotNull(TempGroovyScriptRepositoryContext.getInstance().get("destroy-key-2"));
    }

    @Test
    void destroyWithoutTempScriptsShouldDoNothing() throws Exception {
        runner.destroy();
        assertNull(TempGroovyScriptRepositoryContext.getInstance().get("destroy-key-1"));
    }
}
