package com.codingapi.springboot.flow.matcher;

import com.codingapi.springboot.flow.content.FlowContent;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 操作者匹配器
 */

public class OperatorMatcher {

    @Getter
    private final String script;

    private final Script runtime;

    public OperatorMatcher(String script) {
        if (!StringUtils.hasLength(script)) {
            throw new IllegalArgumentException("script is empty");
        }
        this.script = script;
        GroovyShell groovyShell = new GroovyShell();
        this.runtime = groovyShell.parse(script);
    }

    /**
     * 默认操作者匹配器
     *
     * @return 操作者匹配器
     */
    public static OperatorMatcher anyOperatorMatcher() {
        return new OperatorMatcher("def run(content) {return [content.getCurrentOperator().getUserId()];}");
    }

    /**
     * 匹配操作者
     *
     * @param flowContent 流程内容
     * @return 是否匹配
     */
    public List<Long> matcher(FlowContent flowContent) {
        return (List<Long>) runtime.invokeMethod("run", flowContent);
    }

}
