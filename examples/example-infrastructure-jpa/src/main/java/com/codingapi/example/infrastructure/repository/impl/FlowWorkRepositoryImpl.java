package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FlowWorkRepositoryImpl implements FlowWorkRepository {

    @Override
    public void save(FlowWork flowWork) {

    }

    @Override
    public FlowWork getFlowWorkById(long flowWorkId) {
        return null;
    }
}
