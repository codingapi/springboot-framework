package com.codingapi.springboot.flow.error;

import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.script.GroovyShellContext;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 错误触发器
 */
public class ErrTrigger {

    @Getter
    private final String script;


    public ErrTrigger(String script) {
        if (!StringUtils.hasLength(script)) {
            throw new IllegalArgumentException("script is empty");
        }
        this.script = script;
    }

    /**
     * 触发
     *
     * @param flowSession 流程内容
     */
    public ErrorResult trigger(FlowSession flowSession) {
        return GroovyShellContext.getInstance().run(script, ErrorResult.class, flowSession);
    }

}
