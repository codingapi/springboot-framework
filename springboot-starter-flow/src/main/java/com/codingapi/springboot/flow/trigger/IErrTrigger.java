package com.codingapi.springboot.flow.trigger;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRecord;

/**
 * 异常触发器，当流程发生异常时，将会触发异常触发器，异常触发器可以是一个节点
 */
public interface IErrTrigger {

    /**
     * 触发异常
     * @param record 流程记录
     * @return 异常节点
     */
    FlowNode trigger(FlowRecord record);

}
