package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;

/**
 * 流程设计器仓库
 */
public interface FlowWorkRepository {

    FlowWork getFlowWorkById(long id);

    void save(FlowWork flowWork);

    void delete(long id);

}
