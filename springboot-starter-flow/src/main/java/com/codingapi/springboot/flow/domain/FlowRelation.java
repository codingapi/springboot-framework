package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlowRelation {

    /**
     * 关系id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     *  源节点
     */
    private FlowNode source;

    /**
     * 目标节点
     */
    private FlowNode target;

    /**
     * 出口触发器
     */
    @JsonIgnore
    private IOutTrigger outTrigger;

    /**
     * 设计者
     */
    @JsonIgnore
    private IFlowOperator createUser;

    /**
     *  创建时间
     */
    private long createTime;

    /**
     * 修改时间
     */
    private long updateTime;


}
