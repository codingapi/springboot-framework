package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.repository.impl.DefaultGroovyScriptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

/**
 * GroovyScriptRepositoryContext 与 DefaultGroovyScriptRepository 单元测试
 */
class GroovyScriptRepositoryContextTest {

    private final GroovyScriptRepositoryContext context = GroovyScriptRepositoryContext.getInstance();

    @AfterEach
    void tearDown() {
        // 恢复默认仓储实现，避免 mock 泄漏到其他测试
        context.setGroovyScriptRepository(new DefaultGroovyScriptRepository());
    }

    private GroovyScript script(String key) {
        return GroovyScript.builder(key).script("return 1;").build();
    }

    @Test
    void defaultRepositoryShouldSupportSaveGetDelete() {
        context.save(script("repo-key-1"));

        GroovyScript result = context.get("repo-key-1");
        assertNotNull(result);
        assertSame("repo-key-1", result.getKey());

        context.delete("repo-key-1");
        assertNull(context.get("repo-key-1"));
        // 删除不存在的key不抛异常
        context.delete("repo-key-missing");
    }

    @Test
    void instanceShouldBeSingletonWithDefaultRepository() {
        assertSame(context, GroovyScriptRepositoryContext.getInstance());
        // 默认仓储可用：保存后可读取
        context.save(script("repo-default-key"));
        assertNotNull(context.get("repo-default-key"));
        context.delete("repo-default-key");
    }

    @Test
    void contextShouldDelegateToCustomRepository() {
        GroovyScriptRepository mockRepository = Mockito.mock(GroovyScriptRepository.class);
        GroovyScript groovyScript = script("repo-mock-key");
        Mockito.when(mockRepository.get("repo-mock-key")).thenReturn(groovyScript);

        context.setGroovyScriptRepository(mockRepository);

        context.save(groovyScript);
        assertSame(groovyScript, context.get("repo-mock-key"));
        context.delete("repo-mock-key");

        verify(mockRepository).save(groovyScript);
        verify(mockRepository).get("repo-mock-key");
        verify(mockRepository).delete("repo-mock-key");
    }
}
