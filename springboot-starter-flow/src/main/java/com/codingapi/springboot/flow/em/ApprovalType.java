package com.codingapi.springboot.flow.em;

public enum ApprovalType {

    /**
     * 会签
     */
    SIGN,
    /**
     * 非会签
     */
    UN_SIGN;

    public static ApprovalType parser(String approvalType) {
        for(ApprovalType type:values()){
            if(type.name().equalsIgnoreCase(approvalType)){
                return type;
            }
        }
        return UN_SIGN;
    }
}
