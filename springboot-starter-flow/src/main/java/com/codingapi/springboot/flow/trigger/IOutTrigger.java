package com.codingapi.springboot.flow.trigger;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRecord;

/**
 * 出口触发器，当流程发生异常时，将会触发异常触发器，异常触发器可以是一个节点
 */
public interface IOutTrigger {

    /**
     * 出口触发
     * @param node 节点
     * @return 下一个节点
     */
    FlowNode trigger(FlowRecord record);
}
