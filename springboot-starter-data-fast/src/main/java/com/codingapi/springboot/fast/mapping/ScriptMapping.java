package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.script.ScriptRuntime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Setter
@Getter
public class ScriptMapping extends BaseMapping{

    private String script;

    public ScriptMapping(String mapping, RequestMethod requestMethod, String script) {
        super(mapping, requestMethod);
        this.script = script;
    }

    @ResponseBody
    public Object execute() {
        MvcRunningContext context = MvcRunningContext.getInstance();
        return ScriptRuntime.running(script,context);
    }




}
