package com.codingapi.springboot.flow.serializable;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.trigger.OutTrigger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 流程关系序列化
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlowRelationSerializable implements Serializable {

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
    private String sourceId;

    /**
     * 目标节点
     */
    private String targetId;

    /**
     * 排序
     */
    private int order;

    /**
     * 是否退回
     */
    private boolean back;

    /**
     * 出口触发器
     */
    private String outTrigger;


    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 修改时间
     */
    private long updateTime;

    public FlowRelation toFlowRelation(List<FlowNode> nodes) {
        FlowNode source = null;
        FlowNode target = null;
        for (FlowNode node : nodes) {
            if (node.getId().equals(sourceId)) {
                source = node;
            }
            if (node.getId().equals(targetId)) {
                target = node;
            }
        }
        return new FlowRelation(id, name, source, target, order, back, new OutTrigger(outTrigger), createTime, updateTime);
    }
}
