package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.user.IFlowOperator;

import java.util.List;

/**
 * 流程操作者 仓库
 *
 * @param <ID> ID
 */
public interface FlowOperatorRepository<ID> {

    /**
     * 根据ID查询流程用户
     *
     * @param ids IDs
     * @return List of IFlowOperator
     */
    List<? extends IFlowOperator<ID>> findByIds(List<ID> ids);

}
