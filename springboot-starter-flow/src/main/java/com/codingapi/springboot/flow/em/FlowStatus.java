package com.codingapi.springboot.flow.em;

/**
 * 流程状态
 * 进行中、已完成
 */
public enum FlowStatus {

    /**
     * 进行中
     */
    RUNNING,
    /**
     * 已完成
     */
    FINISH,
    /**
     * 已作废
     */
    VOIDED;


    public static FlowStatus parser(String status) {
        for (FlowStatus flowStatus : FlowStatus.values()) {
            if (flowStatus.name().equalsIgnoreCase(status)) {
                return flowStatus;
            }
        }
        return RUNNING;
    }
}
