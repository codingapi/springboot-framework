package com.codingapi.springboot.script.cache;

import com.codingapi.springboot.framework.event.IHandler;
import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.TestApplication;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GroovyScriptCacheEvent 框架事件桥接测试（issue #214）
 * 验证脚本保存/删除后，进程内业务可通过 IHandler&lt;GroovyScriptCacheEvent&gt; 监听脚本变更事件
 */
@SpringBootTest(classes = TestApplication.class)
class GroovyScriptCacheEventBridgeTest {

    private static final List<GroovyScriptCacheEvent> receivedEvents = new CopyOnWriteArrayList<>();

    @Component
    static class ScriptCacheChangedHandler implements IHandler<GroovyScriptCacheEvent> {

        @Override
        public void handler(GroovyScriptCacheEvent event) {
            receivedEvents.add(event);
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
        receivedEvents.clear();
        GroovyScriptCacheContext.getInstance().clear();
        TempGroovyScriptContext.getInstance().clear();
    }

    @AfterEach
    void tearDown() {
        GroovyScriptCacheContext.getInstance().clear();
        TempGroovyScriptContext.getInstance().clear();
        GroovyScriptRepositoryContext.getInstance().delete("bridge-key");
    }

    @Test
    void saveShouldPushFrameworkEventToListeners() {
        GroovyScript s = script("bridge-key", "return 1;");
        GroovyScriptRepositoryContext.getInstance().save(s);
        GroovyScriptCacheContext.getInstance().getGroovyScript("bridge-key");

        s.setScript("return 2;");
        s.save();

        assertEquals(1, receivedEvents.size());
        GroovyScriptCacheEvent event = receivedEvents.get(0);
        assertEquals(GroovyScriptCacheEvent.Type.SAVE, event.getType());
        assertEquals("bridge-key", event.getKey());
    }

    @Test
    void removeShouldPushFrameworkEventToListeners() {
        GroovyScript s = script("bridge-key", "return 1;");
        GroovyScriptRepositoryContext.getInstance().save(s);
        GroovyScriptCacheContext.getInstance().getGroovyScript("bridge-key");

        s.remove();

        assertEquals(1, receivedEvents.size());
        GroovyScriptCacheEvent event = receivedEvents.get(0);
        assertEquals(GroovyScriptCacheEvent.Type.REMOVE, event.getType());
        assertEquals("bridge-key", event.getKey());
        assertTrue(GroovyScriptRepositoryContext.getInstance().get("bridge-key") == null);
    }
}
