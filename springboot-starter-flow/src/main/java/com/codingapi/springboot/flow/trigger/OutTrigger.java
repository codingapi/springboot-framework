package com.codingapi.springboot.flow.trigger;

import com.codingapi.springboot.flow.content.FlowContent;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 出口触发器
 */

public class OutTrigger {

    @Getter
    private final String script;

    private final Script runtime;

    public OutTrigger(String script) {
        if(!StringUtils.hasLength(script)){
            throw new IllegalArgumentException("script is empty");
        }
        this.script = script;
        GroovyShell groovyShell = new GroovyShell();
        this.runtime = groovyShell.parse(script);
    }

    /**
     * 默认出口触发器
     */
    public static OutTrigger defaultOutTrigger(){
        return new OutTrigger("def run(content) {return true;}");
    }


    /**
     * 触发
     * @param flowContent 流程内容
     * @return 下一个节点 code
     */
    public boolean trigger(FlowContent flowContent){
        return (Boolean) runtime.invokeMethod("run", flowContent);
    }

}
