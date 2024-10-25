package com.codingapi.springboot.flow.matcher;

import com.codingapi.springboot.flow.content.FlowContent;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作者匹配器
 */

public class OperatorMatcher {

    @Getter
    private final String script;

    private final int state;

    private final Script runtime;

    // 指定用户
    public static final int STATE_SPECIFY = 1;
    // 创建者
    public static final int STATE_CREATOR = 2;
    // 任意人
    public static final int STATE_ANY = 3;


    public boolean isAny() {
        return state == STATE_ANY;
    }

    public boolean isCreator() {
        return state == STATE_CREATOR;
    }

    public boolean isSpecify() {
        return state == STATE_SPECIFY;
    }


    public OperatorMatcher(String script) {
        this(script, STATE_SPECIFY);
    }

    public OperatorMatcher(String script, int state) {
        if (!StringUtils.hasLength(script)) {
            throw new IllegalArgumentException("script is empty");
        }
        this.script = script;
        this.state = state;
        GroovyShell groovyShell = new GroovyShell();
        this.runtime = groovyShell.parse(script);
    }

    /**
     * 默认操作者匹配器
     *
     * @return 操作者匹配器
     */
    public static OperatorMatcher anyOperatorMatcher() {
        return new OperatorMatcher("def run(content) {return [content.getCurrentOperator().getUserId()];}", STATE_ANY);
    }

    /**
     * 指定操作者匹配器
     *
     * @param userIds 用户ids
     * @return 操作者匹配器
     */
    public static OperatorMatcher specifyOperatorMatcher(long... userIds) {
        String userIdsStr = Arrays.stream(userIds).mapToObj(String::valueOf).collect(Collectors.joining(","));
        return new OperatorMatcher("def run(content) {return [" + userIdsStr + "];}", STATE_SPECIFY);
    }

    /**
     * 创建者操作者匹配器
     *
     * @param userIds 用户ids
     * @return 操作者匹配器
     */
    public static OperatorMatcher creatorOperatorMatcher() {
        return new OperatorMatcher("def run(content) {return [content.getCreateOperator().getUserId()];}", STATE_CREATOR);
    }

    /**
     * 匹配操作者
     *
     * @param flowContent 流程内容
     * @return 是否匹配
     */
    public List<Long> matcher(FlowContent flowContent) {
        List<Object> values = (List<Object>) runtime.invokeMethod("run", flowContent);
        return values.stream().map(item -> {
            if (item instanceof Number) {
                return ((Number) item).longValue();
            } else {
                return Long.parseLong(item.toString());
            }
        }).collect(Collectors.toList());
    }

}
