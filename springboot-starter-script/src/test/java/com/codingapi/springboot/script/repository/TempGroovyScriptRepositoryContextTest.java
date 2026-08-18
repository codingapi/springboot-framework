package com.codingapi.springboot.script.repository;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.repository.impl.DefaultTempGroovyScriptRepository;
import com.codingapi.springboot.script.temp.TempGroovyScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * TempGroovyScriptRepositoryContext 与 DefaultTempGroovyScriptRepository 单元测试
 * 覆盖分页查询的三个分支（整页/尾页/越界）以及上下文委托
 */
class TempGroovyScriptRepositoryContextTest {

    private final TempGroovyScriptRepositoryContext context = TempGroovyScriptRepositoryContext.getInstance();

    @AfterEach
    void tearDown() {
        for (int i = 0; i < 5; i++) {
            context.delete("temp-page-" + i);
        }
        context.delete("temp-single");
        context.delete("temp-mock-key");
        // 恢复默认仓储实现，避免 mock 泄漏到其他测试
        context.setTempGroovyScriptRepository(new DefaultTempGroovyScriptRepository());
    }

    private TempGroovyScript tempScript(String key, long clearTime) {
        return new TempGroovyScript(GroovyScript.builder(key).script("return 1;").build(), clearTime);
    }

    @Test
    void defaultRepositoryShouldSupportSaveGetDelete() {
        TempGroovyScript tempGroovyScript = tempScript("temp-single", System.currentTimeMillis() + 60000);

        context.save(tempGroovyScript);
        assertSame(tempGroovyScript, context.get("temp-single"));

        context.delete("temp-single");
        assertNull(context.get("temp-single"));
        // 删除不存在的key不抛异常
        context.delete("temp-single-missing");
    }

    @Test
    void findShouldReturnSortedPages() {
        long now = System.currentTimeMillis();
        // clearTime 乱序写入，查询应按 clearTime 升序返回
        context.save(tempScript("temp-page-0", now + 5000));
        context.save(tempScript("temp-page-1", now + 4000));
        context.save(tempScript("temp-page-2", now + 3000));
        context.save(tempScript("temp-page-3", now + 2000));
        context.save(tempScript("temp-page-4", now + 1000));

        // 第一页：满页（size > to 分支）
        Page<TempGroovyScript> firstPage = context.find(PageRequest.of(0, 2));
        assertEquals(5, firstPage.getTotalElements());
        assertTrue(firstPage.hasNext());
        List<TempGroovyScript> firstContent = firstPage.getContent();
        assertEquals(2, firstContent.size());
        assertEquals("temp-page-4", firstContent.get(0).getKey());
        assertEquals("temp-page-3", firstContent.get(1).getKey());

        // 尾页：不足一页（subList(form, list.size()) 分支）
        Page<TempGroovyScript> lastPage = context.find(PageRequest.of(2, 2));
        assertEquals(1, lastPage.getContent().size());
        assertEquals("temp-page-0", lastPage.getContent().get(0).getKey());

        // 越界页：空数据（form > list.size() 分支）
        Page<TempGroovyScript> beyondPage = context.find(PageRequest.of(5, 2));
        assertTrue(beyondPage.getContent().isEmpty());
    }

    @Test
    void contextShouldDelegateToCustomRepository() {
        TempGroovyScriptRepository mockRepository = Mockito.mock(TempGroovyScriptRepository.class);
        TempGroovyScript tempGroovyScript = tempScript("temp-mock-key", System.currentTimeMillis() + 60000);
        Mockito.when(mockRepository.get("temp-mock-key")).thenReturn(tempGroovyScript);

        context.setTempGroovyScriptRepository(mockRepository);

        context.save(tempGroovyScript);
        assertSame(tempGroovyScript, context.get("temp-mock-key"));
        context.delete("temp-mock-key");
        PageRequest pageRequest = PageRequest.of(0, 10);
        context.find(pageRequest);

        verify(mockRepository).save(tempGroovyScript);
        verify(mockRepository).get("temp-mock-key");
        verify(mockRepository).delete("temp-mock-key");
        verify(mockRepository).find(pageRequest);
        assertNotNull(context);
    }
}
