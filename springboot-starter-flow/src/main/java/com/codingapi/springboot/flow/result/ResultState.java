package com.codingapi.springboot.flow.result;

/**
 * 状态数据
 */
public enum ResultState {
    SUCCESS,
    INFO,
    WARNING;


    public static ResultState parser(String state) {
        if ("SUCCESS".equalsIgnoreCase(state)) {
            return SUCCESS;
        } else if ("INFO".equalsIgnoreCase(state)) {
            return INFO;
        } else if ("WARNING".equalsIgnoreCase(state)) {
            return WARNING;
        } else {
            return SUCCESS;
        }
    }
}
