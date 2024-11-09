package com.codingapi.springboot.flow.user;

import lombok.Getter;
import lombok.Setter;

@Getter
public class User implements IFlowOperator{

    @Setter
    private long id;

    private String name;

    private boolean isFlowManager;

    private User entrustOperator;

    public User(String name,boolean isFlowManager) {
        this.name = name;
        this.isFlowManager = isFlowManager;
    }

    public User(String name,User entrustOperator) {
        this.name = name;
        this.entrustOperator = entrustOperator;
    }

    public User(String name) {
        this(name,false);
    }

    @Override
    public IFlowOperator entrustOperator() {
        return entrustOperator;
    }

    @Override
    public long getUserId() {
        return id;
    }

    @Override
    public boolean isFlowManager() {
        return isFlowManager;
    }
}
