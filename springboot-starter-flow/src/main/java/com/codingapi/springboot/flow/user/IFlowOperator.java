package com.codingapi.springboot.flow.user;

public interface IFlowOperator<ID> {

    /**
     * 获取用户ID
     *
     * @return ID
     */
    ID getUserId();

}
