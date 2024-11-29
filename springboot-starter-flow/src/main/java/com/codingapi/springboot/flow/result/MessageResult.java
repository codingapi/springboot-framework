package com.codingapi.springboot.flow.result;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MessageResult {

    /**
     * 展示的标题
     */
    private String title;

    /**
     * 展示状态
     */
    private ResultState resultState;

    /**
     * 展示的内容
     */
    private List<Message> items;

    /**
     * 是否可关闭流程
     */
    private boolean closeable;

    /**
     * 添加一个展示项
     *
     * @param label 标签
     * @param value 值
     * @return this
     */
    public MessageResult addItem(String label, String value) {
        if (items == null) {
            items = new java.util.ArrayList<>();
        }
        items.add(new Message(label, value));
        return this;
    }

    /**
     * 是否可关闭流程
     *
     * @param closeable 是否可关闭
     * @return this
     */
    public MessageResult closeable(boolean closeable) {
        this.closeable = closeable;
        return this;
    }

    /**
     * 展示状态
     *
     * @param resultState 展示状态
     * @return this
     */
    public MessageResult resultState(ResultState resultState) {
        this.resultState = resultState;
        return this;
    }


    @Setter
    @Getter
    @AllArgsConstructor
    public static class Message {
        private String label;
        private String value;
    }

    /**
     * 状态数据
     */
    public enum ResultState {
        SUCCESS,
        INFO,
        WARNING;


        public static ResultState parser(String state) {
            if ("SUCCESS".equalsIgnoreCase(state)) {
                return SUCCESS;
            } else if ("INFO".equalsIgnoreCase(state)) {
                return INFO;
            } else if ("WARNING".equalsIgnoreCase(state)) {
                return WARNING;
            } else {
                return SUCCESS;
            }
        }
    }

    public static MessageResult create(FlowSubmitResult result) {
        List<? extends IFlowOperator> operators = result.getOperators();
        FlowNode flowNode = result.getFlowNode();
        MessageResult messageResult = new MessageResult();
        messageResult.setResultState(ResultState.SUCCESS);
        messageResult.setTitle("下级节点提示");
        messageResult.addItem("下级审批节点", flowNode.getName());
        StringBuilder usernames = new StringBuilder();
        for (IFlowOperator operator : operators) {
            usernames.append(operator.getName()).append(",");
        }
        messageResult.addItem("下级审批人", usernames.toString());
        return messageResult;
    }

    public static MessageResult create(FlowResult result) {
        List<FlowRecord> records = result.getRecords();
        FlowWork flowWork = result.getFlowWork();
        MessageResult messageResult = new MessageResult();
        messageResult.setResultState(ResultState.SUCCESS);
        messageResult.setTitle("流程审批完成");
        for (FlowRecord record : records) {
            FlowNode flowNode = flowWork.getNodeByCode(record.getNodeCode());
            messageResult.addItem("下级审批节点", flowNode.getName());
            messageResult.addItem("下级审批人", record.getCurrentOperator().getName());
        }
        return messageResult;
    }


    public static MessageResult create(String title) {
        return create(title, "SUCCESS", null, false);
    }

    public static MessageResult create(String title, String resultState) {
        return create(title, resultState, null, false);
    }


    public static MessageResult create(String title, String resultState, boolean closeable) {
        return create(title, resultState, null, closeable);
    }

    public static MessageResult create(String title, String resultState, List<Message> items, boolean closeable) {
        MessageResult messageResult = new MessageResult();
        messageResult.setTitle(title);
        messageResult.setItems(items);
        messageResult.setResultState(ResultState.parser(resultState));
        messageResult.setCloseable(closeable);
        return messageResult;
    }
}
