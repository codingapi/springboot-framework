package com.codingapi.springboot.flow.trigger;

import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.script.GroovyShellContext;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 出口触发器
 */
public class OutTrigger {

    @Getter
    private final String script;


    public OutTrigger(String script) {
        if (!StringUtils.hasLength(script)) {
            throw new IllegalArgumentException("script is empty");
        }
        this.script = script;
    }

    /**
     * 默认出口触发器
     */
    public static OutTrigger defaultOutTrigger() {
        return new OutTrigger("def run(content) {return true;}");
    }


    /**
     * 触发
     *
     * @param flowSession 流程内容
     * @return true 进入下一节点，false 则返回上一节点
     */
    public boolean trigger(FlowSession flowSession) {
        return  GroovyShellContext.getInstance().run(script,Boolean.class, flowSession);
    }

}
