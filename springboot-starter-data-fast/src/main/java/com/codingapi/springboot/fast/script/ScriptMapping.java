package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.List;


@Setter
@Getter
public class ScriptMapping {

    private String mapping;
    private ScriptMethod scriptMethod;
    private String script;


    public ScriptMapping(String mapping, ScriptMethod scriptMethod, String script) {
        this.mapping = mapping;
        this.scriptMethod = scriptMethod;
        this.script = script;
    }

    @ResponseBody
    Response execute() {
        Object result = ScriptRuntime.running(script);
        if (result instanceof List || result.getClass().isArray()) {
            return SingleResponse.of(result);
        } else {
            if (result instanceof MultiResponse) {
                return (MultiResponse<?>) result;
            }
            if (result instanceof Page<?>) {
                return MultiResponse.of((Page<?>) result);
            }
            return SingleResponse.of(result);
        }
    }


    Method getExecuteMethod() {
        try {
            return this.getClass().getDeclaredMethod("execute");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


}
