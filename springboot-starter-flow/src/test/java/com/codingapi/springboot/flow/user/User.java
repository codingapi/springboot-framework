package com.codingapi.springboot.flow.user;

import com.codingapi.springboot.flow.operator.IFlowOperator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class User implements IFlowOperator {

    @Setter
    private long id;
    private final String name;

    @Setter
    private String role;

    public User(String name) {
        this.name = name;
        this.role = "user";
    }
}
