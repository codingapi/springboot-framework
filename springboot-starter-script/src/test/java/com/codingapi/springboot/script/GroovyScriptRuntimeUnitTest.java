package com.codingapi.springboot.script;

import com.codingapi.springboot.framework.transaction.TransactionManagerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * GroovyScriptRuntime 单元测试
 * 覆盖编译缓存、LRU 淘汰、invoke/run 各重载以及事务模式分支
 */
class GroovyScriptRuntimeUnitTest {

    private GroovyScriptRuntime runtime;

    private PlatformTransactionManager originalTransactionManager;

    @BeforeEach
    void setUp() {
        runtime = new GroovyScriptRuntime(10);
        originalTransactionManager = TransactionManagerContext.getInstance().getPlatformTransactionManager();
    }

    @AfterEach
    void tearDown() {
        // 恢复事务管理器，避免影响其他测试
        TransactionManagerContext.getInstance().setPlatformTransactionManager(originalTransactionManager);
    }

    private PlatformTransactionManager mockTransactionManager() {
        PlatformTransactionManager txManager = Mockito.mock(PlatformTransactionManager.class);
        TransactionStatus status = Mockito.mock(TransactionStatus.class);
        Mockito.when(txManager.getTransaction(any(TransactionDefinition.class))).thenReturn(status);
        TransactionManagerContext.getInstance().setPlatformTransactionManager(txManager);
        return txManager;
    }

    @Test
    void maxCacheSizeShouldBeConstructorValue() {
        assertEquals(10, runtime.getMaxCacheSize());
    }

    @Test
    void compileWithoutCacheShouldNotStoreEntry() {
        runtime.compile("return 1;", false);
        runtime.compile("return 1;");
        assertEquals(0, runtime.cacheSize());
    }

    @Test
    void compileWithCacheShouldReuseCompiledEntry() {
        runtime.compile("return 1;", true);
        assertEquals(1, runtime.cacheSize());
        // 相同脚本再次编译命中缓存，数量不变
        runtime.compile("return 1;", true);
        assertEquals(1, runtime.cacheSize());
        // 不同脚本新增缓存
        runtime.compile("return 2;", true);
        assertEquals(2, runtime.cacheSize());
    }

    @Test
    void cacheShouldEvictEldestWhenOverMaxSize() {
        GroovyScriptRuntime smallRuntime = new GroovyScriptRuntime(2);
        smallRuntime.run("return 1;", Integer.class, TransactionMode.DEFAULT, null);
        smallRuntime.run("return 2;", Integer.class, TransactionMode.DEFAULT, null);
        smallRuntime.run("return 3;", Integer.class, TransactionMode.DEFAULT, null);
        assertEquals(2, smallRuntime.cacheSize());
    }

    @Test
    void clearCacheShouldRemoveAllCompiledScripts() {
        runtime.compile("return 1;", true);
        runtime.compile("return 2;", true);
        assertEquals(2, runtime.cacheSize());

        runtime.clearCache();
        assertEquals(0, runtime.cacheSize());
    }

    @Test
    void invokeMethodWithArguments() {
        String script = "def run(request){\n"
                + "    return request;\n"
                + "}\n";
        Integer result = runtime.invoke("run", script, Integer.class, 100);
        assertEquals(100, result);
    }

    @Test
    void invokeMethodWithBindsOverload() {
        String script = "def run(request){\n"
                + "    return request + $base;\n"
                + "}\n";
        Map<String, Object> binds = new HashMap<>();
        binds.put("$base", 100);
        Integer result = runtime.invoke("run", script, Integer.class, binds, 23);
        assertEquals(123, result);
    }

    @Test
    void invokeMethodWithDefaultTransactionModeAndBinds() {
        // 脚本体本身即 run() 方法，不能再定义无参 run()（重复签名）
        String script = "return $name;\n";
        Map<String, Object> binds = new HashMap<>();
        binds.put("$name", "hello");
        String result = runtime.invoke("run", script, String.class, TransactionMode.DEFAULT, binds);
        assertEquals("hello", result);
    }

    @Test
    void invokeReadonlyShouldUseTransactionAndRollback() {
        PlatformTransactionManager txManager = mockTransactionManager();
        String script = "def run(request){\n"
                + "    return request;\n"
                + "}\n";

        Integer result = runtime.invoke("run", script, Integer.class, TransactionMode.READONLY, null, 5);
        assertEquals(5, result);
        Mockito.verify(txManager).getTransaction(any(TransactionDefinition.class));
        // 只读模式执行完成后以回滚结束
        Mockito.verify(txManager).rollback(any(TransactionStatus.class));
        Mockito.verify(txManager, Mockito.never()).commit(any(TransactionStatus.class));
    }

    @Test
    void invokeCommitShouldUseTransactionAndCommit() {
        PlatformTransactionManager txManager = mockTransactionManager();
        String script = "def run(request){\n"
                + "    return request * 2;\n"
                + "}\n";

        Integer result = runtime.invoke("run", script, Integer.class, TransactionMode.COMMIT, null, 4);
        assertEquals(8, result);
        Mockito.verify(txManager).commit(any(TransactionStatus.class));
        Mockito.verify(txManager, Mockito.never()).rollback(any(TransactionStatus.class));
    }

    @Test
    void runScriptWithBinds() {
        String script = "return $x + 1;";
        Map<String, Object> binds = new HashMap<>();
        binds.put("$x", 41);
        Integer result = runtime.run(script, Integer.class, binds);
        assertEquals(42, result);
    }

    @Test
    void runScriptWithoutBindsReturnsNullSafely() {
        String script = "return 7;";
        Integer result = runtime.run(script, Integer.class, TransactionMode.DEFAULT, null);
        assertEquals(7, result);
        // binds 为空 Map 同样安全
        Integer result2 = runtime.run(script, Integer.class, TransactionMode.DEFAULT, new HashMap<>());
        assertEquals(7, result2);
    }

    @Test
    void runReadonlyShouldUseTransactionAndRollback() {
        PlatformTransactionManager txManager = mockTransactionManager();

        Integer result = runtime.run("return 9;", Integer.class, TransactionMode.READONLY, null);
        assertEquals(9, result);
        Mockito.verify(txManager).rollback(any(TransactionStatus.class));
        Mockito.verify(txManager, Mockito.never()).commit(any(TransactionStatus.class));
    }

    @Test
    void runCommitShouldUseTransactionAndCommit() {
        PlatformTransactionManager txManager = mockTransactionManager();

        Integer result = runtime.run("return 9;", Integer.class, TransactionMode.COMMIT, null);
        assertEquals(9, result);
        Mockito.verify(txManager).commit(any(TransactionStatus.class));
        Mockito.verify(txManager, Mockito.never()).rollback(any(TransactionStatus.class));
    }

    @Test
    void runShouldReuseCachedScriptInstance() {
        String script = "return 3;";
        Integer first = runtime.run(script, Integer.class, TransactionMode.DEFAULT, null);
        assertEquals(3, first);
        assertEquals(1, runtime.cacheSize());

        Integer second = runtime.run(script, Integer.class, TransactionMode.DEFAULT, null);
        assertEquals(3, second);
        assertEquals(1, runtime.cacheSize());
    }

    @Test
    void invokeResultCanBeNull() {
        // 脚本体本身即 run() 方法，不能再定义无参 run()（重复签名）
        String script = "return null;\n";
        Object result = runtime.invoke("run", script, Object.class);
        assertNull(result);
    }
}
