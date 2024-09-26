package com.codingapi.springboot.flow.em;

/**
 * 分为发起、审批、结束
 */
public enum NodeType {

    /**
     * 发起
     */
    START,
    /**
     * 审批
     */
    APPROVAL,
    /**
     * 结束
     */
    OVER;

}
