package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FlowOperatorRepositoryImpl implements FlowOperatorRepository {

    private final UserRepository userRepository;

    public FlowOperatorRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        FlowRepositoryContext.getInstance().bind(this);
    }

    @Override
    public List<? extends IFlowOperator> findOperatorByIds(List<Long> operatorIds) {
        return userRepository.findUserByIds(operatorIds);
    }

    @Override
    public IFlowOperator getOperatorById(long id) {
        return userRepository.getUserById(id);
    }
}
