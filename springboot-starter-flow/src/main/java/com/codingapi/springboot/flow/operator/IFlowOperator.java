package com.codingapi.springboot.flow.operator;

/**
 * 操作者，流程的操作者，只要实现这个接口，就可以作为流程的操作者
 */
public interface IFlowOperator {
    /**
     * 获取操作者的id
     *
     * @return 操作者的id
     */
    long getId();

    /**
     * 获取操作者的名称
     *
     * @return 操作者的名称
     */
    String getName();

}
