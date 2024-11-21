package com.codingapi.springboot.flow.em;

/**
 *  流程来源的方式
 *  包括 同意、拒绝、转办
 */
public enum FlowSourceDirection {

    /**
     * 同意
     */
    PASS,
    /**
     * 拒绝
     */
    REJECT,
    /**
     * 转办
     */
    TRANSFER;

    public static FlowSourceDirection parser(String type){
        for(FlowSourceDirection flowSourceDirection :values()){
            if(flowSourceDirection.name().equalsIgnoreCase(type)){
                return flowSourceDirection;
            }
        }
        return null;
    }
}
