package com.codingapi.springboot.flow.error;

import com.codingapi.springboot.flow.content.FlowContent;

/**
 * 错误触发器
 */
public interface IErrTrigger {

    /**
     * 触发
     * @param flowContent 流程内容
     */
    ErrorResult trigger(FlowContent flowContent);

}
