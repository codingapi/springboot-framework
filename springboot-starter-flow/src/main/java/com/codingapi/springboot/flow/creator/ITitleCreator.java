package com.codingapi.springboot.flow.creator;

import com.codingapi.springboot.flow.domain.FlowRecord;

/**
 * 标题创建器，
 * 用于创建标题，根据当前节点的配置设置自定义的标题 ，默认标题为 流程名称-节点名称-审批人名称
 */
public interface ITitleCreator {

    /**
     * 创建标题
     * @param record 流程记录
     * @return 标题
     */
    String createTitle(FlowRecord record);

}
