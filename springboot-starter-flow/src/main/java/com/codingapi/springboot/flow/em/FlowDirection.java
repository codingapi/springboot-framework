package com.codingapi.springboot.flow.em;

/**
 * 流转方式
 *  包括 同意、拒绝、转办
 */
public enum FlowDirection {

    /**
     * 同意
     */
    PASS,
    /**
     * 拒绝
     */
    REJECT,
    /**
     *
     */
    TRANSFER;

    public static FlowDirection parser(String type){
        for(FlowDirection flowDirection :values()){
            if(flowDirection.name().equalsIgnoreCase(type)){
                return flowDirection;
            }
        }
        return PASS;
    }
}
