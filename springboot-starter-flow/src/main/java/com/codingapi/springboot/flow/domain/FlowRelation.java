package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.trigger.OutTrigger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 流程关系
 */
@Getter
@AllArgsConstructor
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
    private OutTrigger outTrigger;


    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 修改时间
     */
    private long updateTime;


    /**
     * 匹配节点
     * @param nodeCode 节点编码
     * @return 是否匹配
     */
    public boolean sourceMatcher(String nodeCode){
        return source.getCode().equals(nodeCode);
    }


    /**
     * 重新排序
     * @param order 排序
     */
    public void resort(int order){
        this.order = order;
    }


    public FlowRelation(String id, String name, FlowNode source, FlowNode target, OutTrigger outTrigger,int order,boolean back) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.target = target;
        this.outTrigger = outTrigger;
        this.order = order;
        this.back = back;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.verify();
    }

    /**
     * 触发条件
     * @param flowContent 流程内容
     * @return 下一个节点
     */
    public FlowNode trigger(FlowContent flowContent){
        if(outTrigger.trigger(flowContent)){
            return target;
        }
        return null;
    }


    /**
     * 验证
     */
    private void verify() {

        if (!StringUtils.hasLength(id)) {
            throw new RuntimeException("id is null");
        }

        if (source == null || target == null) {
            throw new RuntimeException("source or target is null");
        }

        if (outTrigger == null) {
            throw new RuntimeException("outTrigger is null");
        }
    }
}
