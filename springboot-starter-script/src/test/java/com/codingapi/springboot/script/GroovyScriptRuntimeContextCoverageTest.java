package com.codingapi.springboot.script;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GroovyScriptRuntimeContext 单元测试
 * 覆盖单例上下文对 GroovyScriptRuntime 的委托方法
 */
class GroovyScriptRuntimeContextCoverageTest {

    @AfterEach
    void tearDown() {
        GroovyScriptRuntimeContext.getInstance().clearCache();
    }

    @Test
    void instanceShouldBeSingletonWithDefaultConfig() {
        GroovyScriptRuntimeContext context = GroovyScriptRuntimeContext.getInstance();
        assertSame(context, GroovyScriptRuntimeContext.getInstance());
        // 默认配置 shellMaxCacheSize = 10 * 1024
        assertTrue(context.getMaxCacheSize() > 0);
        assertEquals(GroovyScriptRuntimeContext.getInstance().getMaxCacheSize(), context.getMaxCacheSize());
    }

    @Test
    void compileWithAndWithoutCache() {
        GroovyScriptRuntimeContext context = GroovyScriptRuntimeContext.getInstance();

        context.compile("return 1;");
        assertEquals(0, context.cacheSize());

        context.compile("return 2;", true);
        assertEquals(1, context.cacheSize());

        context.clearCache();
        assertEquals(0, context.cacheSize());
    }

    @Test
    void runShouldDelegateToRuntime() {
        GroovyScriptRuntimeContext context = GroovyScriptRuntimeContext.getInstance();

        Integer result = context.run("return 7;", Integer.class, TransactionMode.DEFAULT, null);
        assertEquals(7, result);

        Map<String, Object> binds = new HashMap<>();
        binds.put("$x", 1);
        Integer bound = context.run("return $x + 1;", Integer.class, TransactionMode.DEFAULT, binds);
        assertEquals(2, bound);
    }

    @Test
    void invokeShouldDelegateToRuntime() {
        GroovyScriptRuntimeContext context = GroovyScriptRuntimeContext.getInstance();

        String script = "def run(request){\n"
                + "    return request;\n"
                + "}\n";
        Integer result = context.invoke("run", script, Integer.class, TransactionMode.DEFAULT, null, 100);
        assertEquals(100, result);
    }
}
