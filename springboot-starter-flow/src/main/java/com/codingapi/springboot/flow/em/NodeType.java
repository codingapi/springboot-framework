package com.codingapi.springboot.flow.em;

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



    public static NodeType parser(String type){
        for(NodeType nodeType:values()){
            if(nodeType.name().equalsIgnoreCase(type)){
                return nodeType;
            }
        }
        return APPROVAL;
    }
}
