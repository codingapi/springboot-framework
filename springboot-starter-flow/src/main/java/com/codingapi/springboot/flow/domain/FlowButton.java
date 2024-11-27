package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.em.FlowButtonType;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.result.MessageResult;
import com.codingapi.springboot.flow.script.GroovyShellContext;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 流程按钮
 */
@Setter
@Getter
public class FlowButton {

    /**
     * 编号
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 样式
     */
    private String style;
    /**
     * 事件类型
     */
    private FlowButtonType type;
    /**
     * 自定义事件内容 （后端脚本）
     */
    private String groovy;
    /**
     * 排序
     */
    private int order;

    public boolean isGroovy() {
        return groovy != null;
    }

    /**
     * 执行按钮事件
     * @param flowRecord      流程记录
     * @param flowNode        节点
     * @param flowWork        流程设计器
     * @param createOperator  创建者
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         意见
     * @param historyRecords  历史记录
     */
    public MessageResult run(FlowRecord flowRecord,
                             FlowNode flowNode,
                             FlowWork flowWork,
                             IFlowOperator createOperator,
                             IFlowOperator currentOperator,
                             IBindData bindData,
                             Opinion opinion,
                             List<FlowRecord> historyRecords) {
        if (groovy != null) {
            //执行脚本
            FlowSession session = new FlowSession(flowRecord, flowWork, flowNode, createOperator, currentOperator, bindData, opinion, historyRecords);
            GroovyShellContext.ShellScript script = GroovyShellContext.getInstance().parse(groovy);
            return (MessageResult) script.invokeMethod("run", session);
        }
        return null;
    }
}
