package com.codingapi.springboot.flow.generator;

import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.script.GroovyShellContext;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 标题生成器
 */
public class TitleGenerator {

    @Getter
    private final String script;

    private final GroovyShellContext.ShellScript runtime;

    public TitleGenerator(String script) {
        if (!StringUtils.hasLength(script)) {
            throw new IllegalArgumentException("script is empty");
        }
        this.script = script;
        this.runtime = GroovyShellContext.getInstance().parse(script);
    }


    /**
     * 默认标题生成器
     *
     * @return 标题生成器
     */
    public static TitleGenerator defaultTitleGenerator() {
        return new TitleGenerator("def run(content){ return content.getCurrentOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}");
    }


    /**
     * 生成标题
     *
     * @param flowSession 流程内容
     * @return 标题
     */
    public String generate(FlowSession flowSession) {
        return (String) this.runtime.invokeMethod("run", flowSession);
    }

}
