package com.codingapi.springboot.flow.em;

/**
 * 流转类型
 */
public enum RecodeType {

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


    public static RecodeType parser(String type){
        for(RecodeType recodeType:values()){
            if(recodeType.name().equalsIgnoreCase(type)){
                return recodeType;
            }
        }
        return TODO;
    }
}
