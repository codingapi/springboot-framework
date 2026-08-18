package com.codingapi.springboot.script;

import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GroovyScript 单元测试
 * 覆盖 Builder 全字段、copy、temp/save/remove 生命周期以及 run/invoke 各重载
 */
class GroovyScriptTest {

    @BeforeEach
    void setUp() {
        TempGroovyScriptContext.getInstance().clear();
    }

    @AfterEach
    void tearDown() {
        TempGroovyScriptContext.getInstance().clear();
        GroovyScriptRepositoryContext.getInstance().delete("lifecycle-key");
    }

    @Test
    void builderShouldSetAllFields() {
        Map<String, Class<?>> binds = new HashMap<>();
        binds.put("$x", Integer.class);
        Map<String, Class<?>> requests = new HashMap<>();
        requests.put("request", String.class);

        long before = System.currentTimeMillis();
        GroovyScript script = GroovyScript.builder("builder-key")
                .script("return 1;")
                .description("desc")
                .method("run")
                .returnType(Integer.class)
                .binds(binds)
                .requests(requests)
                .typeOne("one")
                .typeTwo("two")
                .tag("tag")
                .remark("remark")
                .build();

        assertEquals("builder-key", script.getKey());
        assertEquals("return 1;", script.getScript());
        assertEquals("desc", script.getDescription());
        assertEquals("run", script.getMethod());
        assertEquals(Integer.class, script.getReturnType());
        assertSame(binds, script.getBinds());
        assertSame(requests, script.getRequests());
        assertEquals("one", script.getTypeOne());
        assertEquals("two", script.getTypeTwo());
        assertEquals("tag", script.getTag());
        assertEquals("remark", script.getRemark());
        assertTrue(script.getCreateTime() >= before);
        assertEquals(0, script.getUpdateTime());
    }

    @Test
    void singleArgConstructorShouldOnlySetKeyAndCreateTime() {
        GroovyScript script = new GroovyScript("ctor-key");
        assertEquals("ctor-key", script.getKey());
        assertTrue(script.getCreateTime() > 0);
        assertNull(script.getScript());
    }

    @Test
    void copyShouldKeepAllFieldsWithNewKey() {
        Map<String, Class<?>> binds = new HashMap<>();
        binds.put("$x", Integer.class);

        GroovyScript origin = GroovyScript.builder("origin-key")
                .script("return 1;")
                .description("desc")
                .method("run")
                .returnType(Integer.class)
                .binds(binds)
                .typeOne("one")
                .typeTwo("two")
                .tag("tag")
                .remark("remark")
                .build();
        origin.setUpdateTime(123L);

        GroovyScript copy = origin.copy("copy-key");

        assertEquals("copy-key", copy.getKey());
        assertEquals(origin.getScript(), copy.getScript());
        assertEquals(origin.getDescription(), copy.getDescription());
        assertEquals(origin.getMethod(), copy.getMethod());
        assertEquals(origin.getReturnType(), copy.getReturnType());
        assertSame(origin.getBinds(), copy.getBinds());
        assertEquals(origin.getTypeOne(), copy.getTypeOne());
        assertEquals(origin.getTypeTwo(), copy.getTypeTwo());
        assertEquals(origin.getTag(), copy.getTag());
        assertEquals(origin.getRemark(), copy.getRemark());
        assertEquals(origin.getCreateTime(), copy.getCreateTime());
        assertEquals(origin.getUpdateTime(), copy.getUpdateTime());
    }

    @Test
    void tempSaveRemoveLifecycle() {
        GroovyScript script = GroovyScript.builder("lifecycle-key")
                .script("return 1;")
                .build();

        // 临时存储
        script.temp();
        assertNotNull(TempGroovyScriptContext.getInstance().getGroovyScript("lifecycle-key"));

        // 保存后进入仓储，临时数据被清理
        script.save();
        assertTrue(script.getUpdateTime() > 0);
        assertSame(script, GroovyScriptRepositoryContext.getInstance().get("lifecycle-key"));
        assertNull(TempGroovyScriptContext.getInstance().getGroovyScript("lifecycle-key"));

        // 删除后仓储与临时均无数据
        script.remove();
        assertNull(GroovyScriptRepositoryContext.getInstance().get("lifecycle-key"));
        assertNull(TempGroovyScriptContext.getInstance().getGroovyScript("lifecycle-key"));
    }

    @Test
    void runOverloads() {
        GroovyScript plain = GroovyScript.builder("run-key")
                .script("return 7;")
                .returnType(Integer.class)
                .build();

        assertEquals(Integer.valueOf(7), plain.<Integer>run());
        assertEquals(Integer.valueOf(7), plain.<Integer>run(TransactionMode.DEFAULT));

        GroovyScript bound = GroovyScript.builder("run-bind-key")
                .script("return $x + 1;")
                .returnType(Integer.class)
                .build();
        Map<String, Object> binds = new HashMap<>();
        binds.put("$x", 41);
        assertEquals(Integer.valueOf(42), bound.<Integer>run(binds));
        assertEquals(Integer.valueOf(42), bound.<Integer>run(TransactionMode.DEFAULT, binds));
    }

    @Test
    void invokeOverloads() {
        String script = "def run(request){\n"
                + "    return request + offset;\n"
                + "}\n";
        GroovyScript groovyScript = GroovyScript.builder("invoke-key")
                .script(script)
                .method("run")
                .returnType(Integer.class)
                .build();

        // invoke(requests...)
        Map<String, Object> binds = new HashMap<>();
        binds.put("offset", 0);
        assertEquals(Integer.valueOf(10), groovyScript.<Integer>invoke(binds, 10));
        // invoke(transactionMode, requests...)
        assertEquals(Integer.valueOf(11), groovyScript.<Integer>invoke(TransactionMode.DEFAULT, binds, 11));
        // invoke(transactionMode, binds, requests...)
        assertEquals(Integer.valueOf(12),
                groovyScript.<Integer>invoke(TransactionMode.DEFAULT, binds, 12));

        // 无参数函数：脚本体本身即 run() 方法，不能再定义无参 run()（重复签名）
        String noArgScript = "return 99;\n";
        GroovyScript noArg = GroovyScript.builder("invoke-noarg-key")
                .script(noArgScript)
                .method("run")
                .returnType(Integer.class)
                .build();
        assertEquals(Integer.valueOf(99), noArg.<Integer>invoke());
        assertEquals(Integer.valueOf(99), noArg.<Integer>invoke(TransactionMode.DEFAULT));
    }

    @Test
    void compileShouldDelegateToRuntimeContext() {
        GroovyScript script = GroovyScript.builder("compile-key")
                .script("return 5;")
                .build();

        script.compile();
        script.compile(true);
        assertTrue(GroovyScriptRuntimeContext.getInstance().cacheSize() >= 1);
        GroovyScriptRuntimeContext.getInstance().clearCache();
    }

    @Test
    void toMetadataShouldReturnScannedMetadata() {
        GroovyScript script = GroovyScript.builder("meta-key")
                .script("return 1;")
                .description("desc")
                .method("run")
                .returnType(Integer.class)
                .build();

        GroovyMetadata metadata = script.toMetadata();
        assertNotNull(metadata);
        assertEquals("run", metadata.getMainMethod());
        assertEquals("Integer", metadata.getReturnType());
        assertEquals("desc", metadata.getDescription());
    }
}
