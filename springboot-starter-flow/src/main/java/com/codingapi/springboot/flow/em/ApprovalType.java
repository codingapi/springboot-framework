package com.codingapi.springboot.flow.em;

/**
 *  审批类型：会签与非会签
 */
public enum ApprovalType {

    /**
     * 会签
     */
    SIGN,
    /**
     * 非会签
     */
    UN_SIGN,
    /**
     * 传阅
     */
    CIRCULATE;


    public static ApprovalType parser(String approvalType) {
        for(ApprovalType type:values()){
            if(type.name().equalsIgnoreCase(approvalType)){
                return type;
            }
        }
        return UN_SIGN;
    }
}
