package com.codingapi.springboot.flow.em;

/**
 * 流程审批类型
 */
public enum FlowType {

    /**
     * 会签
     */
    SIGN,
    /**
     * 非会签
     */
    NOT_SIGN;


    public static FlowType parser(String type) {
        if (type.equals("SIGN")) {
            return SIGN;
        } else {
            return NOT_SIGN;
        }
    }
}
