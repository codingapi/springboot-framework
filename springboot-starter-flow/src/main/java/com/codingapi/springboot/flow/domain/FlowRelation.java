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
     * 源节点
     */
    private FlowNode source;

    /**
     * 目标节点
     */
    private FlowNode target;


    /**
     * 是否默认出口
     */
    private boolean defaultOut;

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
     * 创建时间
     */
    private long createTime;

    /**
     * 修改时间
     */
    private long updateTime;


    public FlowRelation(String id, FlowNode source, FlowNode target, IOutTrigger outTrigger, IFlowOperator createUser, boolean defaultOut) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.outTrigger = outTrigger;
        this.createUser = createUser;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.defaultOut = defaultOut;
    }


    public FlowNode trigger(FlowRecord record){
        return outTrigger.trigger(record);
    }

}
