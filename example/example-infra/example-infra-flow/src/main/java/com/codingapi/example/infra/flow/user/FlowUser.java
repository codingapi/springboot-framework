package com.codingapi.example.infra.flow.user;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.springboot.flow.user.IFlowOperator;

public class FlowUser implements IFlowOperator {

    private final User user;

    public FlowUser(User user) {
        this.user = user;
    }

    @Override
    public long getUserId() {
        return user.getId();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public boolean isFlowManager() {
        return user.isFlowManager();
    }

    @Override
    public IFlowOperator entrustOperator() {
        if(user.getEntrustOperator()!=null) {
            return new FlowUser(user.getEntrustOperator());
        }else {
            return null;
        }
    }
}
