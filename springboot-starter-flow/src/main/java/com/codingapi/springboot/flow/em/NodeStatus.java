package com.codingapi.springboot.flow.em;

/**
 * 节点状态 | 待办、已办、转办
 */
public enum NodeStatus {

    /**
     * 待办
     */
    TODO,
    /**
     * 已办
     */
    DONE,
    /**
     * 转办
     */
    TRANSFER;
}
