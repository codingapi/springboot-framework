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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groovy-script")
public class GroovyScriptController {

    @PostMapping("/compile")
    public Response compile(@RequestBody ScriptCompileRequest request) {
        try {
            GroovyScriptRuntimeContext.getInstance().compile(request.getScript(), request.isCache());
            return Response.buildSuccess();
        } catch (Exception exception) {
            throw new LocaleMessageException("script.compile.error", "脚本编译异常:" + exception.getMessage());
        }
    }

    @GetMapping("/getScript")
    public SingleResponse<String> getScript(@RequestParam(name = "key") String key) {
        GroovyScript groovyScript = GroovyScriptCacheContext.getInstance().getGroovyScript(key);
        if (groovyScript != null) {
            return SingleResponse.of(groovyScript.getScript());
        }
        throw new LocaleMessageException("script.null", "脚本对象不存在");
    }

    @GetMapping("/getMetadata")
    public SingleResponse<GroovyMetadata> getMetadata(@RequestParam(name = "key") String key) {
        GroovyScript groovyScript = GroovyScriptCacheContext.getInstance().getGroovyScript(key);
        if (groovyScript != null) {
            return SingleResponse.of(groovyScript.toMetadata());
        }
        throw new LocaleMessageException("script.null", "脚本对象不存在");
    }


    @PostMapping("/save")
    public Response save(@RequestBody ScriptSaveRequest request) {
        try {
            GroovyScript groovyScript = GroovyScriptCacheContext.getInstance().getGroovyScript(request.getKey());
            if (groovyScript != null) {
                groovyScript.setScript(request.getScript());
                groovyScript.compile(true);
                groovyScript.save();
                return Response.buildSuccess();
            }
            throw new LocaleMessageException("script.null", "脚本对象不存在");
        } catch (Exception exception) {
            throw new LocaleMessageException("script.compile.error", "脚本编译异常:" + exception.getMessage());
        }
    }

}
