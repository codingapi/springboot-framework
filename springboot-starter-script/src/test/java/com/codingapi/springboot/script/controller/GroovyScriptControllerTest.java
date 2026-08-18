package com.codingapi.springboot.script.controller;

import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import com.codingapi.springboot.framework.exception.LocaleMessageException;
import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.GroovyScriptRuntimeContext;
import com.codingapi.springboot.script.cache.GroovyScriptCacheContext;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.pojo.ScriptCompileRequest;
import com.codingapi.springboot.script.pojo.ScriptSaveRequest;
import com.codingapi.springboot.script.repository.GroovyScriptRepositoryContext;
import com.codingapi.springboot.script.temp.TempGroovyScriptContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GroovyScriptController 单元测试
 * 直接调用 Controller 方法，覆盖编译/查询/保存 REST 接口逻辑
 */
class GroovyScriptControllerTest {

    private final GroovyScriptController controller = new GroovyScriptController();

    private GroovyScript script(String key, String content) {
        return GroovyScript.builder(key)
                .script(content)
                .description("desc-" + key)
                .method("run")
                .returnType(Integer.class)
                .build();
    }

    @BeforeEach
    void setUp() {
        GroovyScriptCacheContext.getInstance().clear();
        TempGroovyScriptContext.getInstance().clear();
        GroovyScriptRuntimeContext.getInstance().clearCache();
    }

    @AfterEach
    void tearDown() {
        GroovyScriptCacheContext.getInstance().clear();
        TempGroovyScriptContext.getInstance().clear();
        GroovyScriptRuntimeContext.getInstance().clearCache();
        GroovyScriptRepositoryContext.getInstance().delete("ctrl-save-key");
        GroovyScriptRepositoryContext.getInstance().delete("ctrl-repo-save-key");
    }

    @Test
    void compileSuccessWithoutCache() {
        ScriptCompileRequest request = new ScriptCompileRequest();
        request.setScript("return 1;");
        request.setCache(false);

        Response response = controller.compile(request);
        assertTrue(response.isSuccess());
    }

    @Test
    void compileSuccessWithCache() {
        ScriptCompileRequest request = new ScriptCompileRequest();
        request.setScript("return 2;");
        request.setCache(true);

        Response response = controller.compile(request);
        assertTrue(response.isSuccess());
        assertEquals(1, GroovyScriptRuntimeContext.getInstance().cacheSize());
    }

    @Test
    void compileFailureShouldThrowCompileErrorException() {
        ScriptCompileRequest request = new ScriptCompileRequest();
        request.setScript("def {{{");
        request.setCache(false);

        LocaleMessageException exception =
                assertThrows(LocaleMessageException.class, () -> controller.compile(request));
        assertEquals("script.compile.error", exception.getErrCode());
        assertTrue(exception.getErrMessage().contains("脚本编译异常"));
    }

    @Test
    void getScriptShouldReturnContentWhenScriptExists() {
        GroovyScriptCacheContext.getInstance().save(script("ctrl-get-key", "return 3;"));

        SingleResponse<String> response = controller.getScript("ctrl-get-key");
        assertTrue(response.isSuccess());
        assertEquals("return 3;", response.getData());
    }

    @Test
    void getScriptShouldThrowWhenScriptMissing() {
        LocaleMessageException exception =
                assertThrows(LocaleMessageException.class, () -> controller.getScript("ctrl-no-such-key"));
        assertEquals("script.null", exception.getErrCode());
    }

    @Test
    void getMetadataShouldReturnMetadataWhenScriptExists() {
        GroovyScriptCacheContext.getInstance().save(script("ctrl-meta-key", "return 4;"));

        SingleResponse<GroovyMetadata> response = controller.getMetadata("ctrl-meta-key");
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("run", response.getData().getMainMethod());
        assertEquals("Integer", response.getData().getReturnType());
    }

    @Test
    void getMetadataShouldThrowWhenScriptMissing() {
        LocaleMessageException exception =
                assertThrows(LocaleMessageException.class, () -> controller.getMetadata("ctrl-no-such-key"));
        assertEquals("script.null", exception.getErrCode());
    }

    @Test
    void saveShouldUpdateTempScript() {
        GroovyScript tempScript = script("ctrl-save-key", "return 1;");
        tempScript.temp();

        ScriptSaveRequest request = new ScriptSaveRequest();
        request.setKey("ctrl-save-key");
        request.setScript("return 10;");

        Response response = controller.save(request);
        assertTrue(response.isSuccess());
        assertEquals("return 10;",
                TempGroovyScriptContext.getInstance().getGroovyScript("ctrl-save-key").getScript());
    }

    @Test
    void saveShouldUpdatePersistentScriptWhenNotInTemp() {
        // 仅存在于仓储中的脚本（经由缓存上下文回源获取）
        GroovyScriptRepositoryContext.getInstance().save(script("ctrl-repo-save-key", "return 1;"));

        ScriptSaveRequest request = new ScriptSaveRequest();
        request.setKey("ctrl-repo-save-key");
        request.setScript("return 20;");

        Response response = controller.save(request);
        assertTrue(response.isSuccess());

        GroovyScript saved = GroovyScriptRepositoryContext.getInstance().get("ctrl-repo-save-key");
        assertNotNull(saved);
        assertEquals("return 20;", saved.getScript());
        assertTrue(saved.getUpdateTime() > 0);
    }

    @Test
    void saveShouldThrowWhenScriptMissing() {
        ScriptSaveRequest request = new ScriptSaveRequest();
        request.setKey("ctrl-no-such-key");
        request.setScript("return 30;");

        // 注意：源码中 script.null 异常被外层 catch 捕获后重新包装为 script.compile.error
        LocaleMessageException exception =
                assertThrows(LocaleMessageException.class, () -> controller.save(request));
        assertEquals("script.compile.error", exception.getErrCode());
        assertTrue(exception.getErrMessage().contains("脚本对象不存在"));
    }

    @Test
    void saveShouldThrowCompileErrorWhenNewScriptInvalid() {
        GroovyScript tempScript = script("ctrl-save-key", "return 1;");
        tempScript.temp();

        ScriptSaveRequest request = new ScriptSaveRequest();
        request.setKey("ctrl-save-key");
        request.setScript("def {{{");

        LocaleMessageException exception =
                assertThrows(LocaleMessageException.class, () -> controller.save(request));
        assertEquals("script.compile.error", exception.getErrCode());
    }
}
