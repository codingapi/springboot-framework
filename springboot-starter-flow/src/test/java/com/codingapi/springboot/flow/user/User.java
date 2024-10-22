package com.codingapi.springboot.flow.user;

import lombok.Getter;
import lombok.Setter;

@Getter
public class User implements IFlowOperator<Long>{

    @Setter
    private long id;

    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public Long getUserId() {
        return id;
    }
}
