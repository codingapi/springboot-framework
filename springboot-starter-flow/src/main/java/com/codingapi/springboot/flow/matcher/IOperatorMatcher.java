package com.codingapi.springboot.flow.matcher;

import com.codingapi.springboot.flow.content.FlowContent;

import java.util.List;

/**
 * 操作者匹配器
 */
public interface IOperatorMatcher {


    /**
     * 匹配操作者
     * @param flowContent 流程内容
     * @return 是否匹配
     */
    List<Long> matcher(FlowContent flowContent);

}
