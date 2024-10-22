package com.codingapi.springboot.flow.generator;

import com.codingapi.springboot.flow.content.FlowContent;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 标题生成器
 */
public class TitleGenerator {

    @Getter
    private final String script;

    private final Script runtime;

    public TitleGenerator(String script) {
        if (!StringUtils.hasLength(script)) {
            throw new IllegalArgumentException("script is empty");
        }
        this.script = script;
        GroovyShell groovyShell = new GroovyShell();
        this.runtime = groovyShell.parse(script);
    }


    /**
     * 默认标题生成器
     *
     * @return 标题生成器
     */
    public static TitleGenerator defaultTitleGenerator() {
        return new TitleGenerator("def run(content){ return content.getCreateOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}");
    }


    /**
     * 生成标题
     *
     * @param flowContent 流程内容
     * @return 标题
     */
    public String generate(FlowContent flowContent) {
        return (String) this.runtime.invokeMethod("run", flowContent);
    }

}
