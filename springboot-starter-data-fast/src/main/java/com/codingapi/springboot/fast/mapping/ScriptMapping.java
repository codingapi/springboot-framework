package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.fast.script.ScriptRuntime;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Setter
@Getter
public class ScriptMapping extends BaseMapping{

    private String script;

    public ScriptMapping(String mapping, RequestMethod requestMethod, String script) {
        super(mapping, requestMethod);
        this.script = script;
    }

    @ResponseBody
    public Response execute() {
        MvcRunningContext context = MvcRunningContext.getInstance();
        Object result = ScriptRuntime.running(script,context);
        if(result instanceof List || result.getClass().isArray()){
            return SingleResponse.of(result);
        }else{
            if(result instanceof MultiResponse){
                return (MultiResponse<?>)result;
            }
            if(result instanceof Page<?>){
                return MultiResponse.of((Page<?>) result);
            }
            return SingleResponse.of(result);
        }
    }




}
