package com.codingapi.example.domain;

import com.codingapi.springboot.flow.operator.IFlowOperator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User implements IFlowOperator {

    private long id;

    private String name;

    private String username;
    private String password;

    public User(String name) {
        this.name = name;
    }
}
