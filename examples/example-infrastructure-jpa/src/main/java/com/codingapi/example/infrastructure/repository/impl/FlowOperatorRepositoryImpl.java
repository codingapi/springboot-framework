package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.infrastructure.jpa.UserEntityRepository;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FlowOperatorRepositoryImpl implements FlowOperatorRepository {

    private final UserRepository userRepository;

    @Override
    public List<? extends IFlowOperator> findOperatorByIds(List<Long> operatorIds) {
        return userRepository.findUserByIds(operatorIds);
    }

    @Override
    public IFlowOperator getOperatorById(long id) {
        return userRepository.getUserById(id);
    }
}
