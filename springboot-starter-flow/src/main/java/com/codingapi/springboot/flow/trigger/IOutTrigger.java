package com.codingapi.springboot.flow.trigger;

import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.domain.FlowNode;

/**
 * 出口触发器
 */
public interface IOutTrigger {

    /**
     * 触发
     * @param flowContent 流程内容
     * @return 下一个节点
     */
    FlowNode trigger(FlowContent flowContent);

}
