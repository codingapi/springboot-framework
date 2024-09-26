package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements FlowOperatorRepository {

    private final List<User> operators = new ArrayList<>();

    public void save(User user) {
        if (user.getId() == 0) {
            operators.add(user);
            user.setId(operators.size());
        }
    }

    @Override
    public IFlowOperator getOperatorById(long id) {
        return operators.stream().filter(operator -> operator.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<User> findOperatorByIds(List<Long> operatorIds) {
        return operators.stream().filter(operator -> operatorIds.contains(operator.getId())).toList();
    }
}
