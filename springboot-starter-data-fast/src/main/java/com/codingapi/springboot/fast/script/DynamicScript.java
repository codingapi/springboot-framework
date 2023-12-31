package com.codingapi.springboot.fast.script;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;

@Setter
@Getter
public class DynamicScript {

    private String script;
    private String mapping;
    private RequestMethod requestMethod;


    @ResponseBody
    public Object execute() {
        ScriptContext context = ScriptContext.getInstance();
        return ScriptRuntime.running(script,context);
    }

    public Method getExecuteMethod() {
        try {
            return this.getClass().getDeclaredMethod("execute");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


}
