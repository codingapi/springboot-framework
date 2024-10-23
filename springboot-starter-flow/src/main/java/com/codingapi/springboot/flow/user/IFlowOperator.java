package com.codingapi.springboot.flow.user;

public interface IFlowOperator {

    /**
     * 获取用户ID
     *
     * @return ID
     */
    long getUserId();


    /**
     * 获取用户名称
     * @return 名称
     */
    String getName();


    /**
     * 是否流程管理员
     *
     * 流程管理员可以强制干预流程
     */
    boolean isFlowManager();

}
