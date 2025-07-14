package com.codingapi.springboot.flow.em;

/**
 * 流程的类型
 */
public enum FlowType {

    /**
     * 待办
     */
    TODO,
    /**
     * 已办
     */
    DONE,
    /**
     * 传阅
     */
    CIRCULATE,
    /**
     * 转办
     */
    TRANSFER,
    /**
     * 等待执行
     */
    WAITING,
    /**
     * 删除
     */
    DELETE;


    public static FlowType parser(String type){
        for(FlowType flowType :values()){
            if(flowType.name().equalsIgnoreCase(type)){
                return flowType;
            }
        }
        return TODO;
    }
}
