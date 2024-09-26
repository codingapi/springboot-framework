package com.codingapi.springboot.flow.em;

/**
 * 记录状态 | 正常、异常，当流程发生异常时，将会记录异常状态，异常状态的流程将无法继续审批
 */
public enum RecodeState {

    /**
     * 正常
     */
    NORMAL,
    /**
     * 异常
     */
    ERROR;
}
