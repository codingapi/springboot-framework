package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.trigger.IOutTrigger;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 流程关系
 */
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
    private IOutTrigger outTrigger;


    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 修改时间
     */
    private long updateTime;

    public FlowRelation(String id, String name, FlowNode source, FlowNode target, IOutTrigger outTrigger, boolean defaultOut) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.target = target;
        this.defaultOut = defaultOut;
        this.outTrigger = outTrigger;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.verify();
    }


    private void verify() {

        if (!StringUtils.hasLength(id)) {
            throw new RuntimeException("id is null");
        }

        if (!StringUtils.hasLength(name)) {
            throw new RuntimeException("name is null");
        }

        if (source == null || target == null) {
            throw new RuntimeException("source or target is null");
        }

        if (outTrigger == null) {
            throw new RuntimeException("outTrigger is null");
        }
    }
}
