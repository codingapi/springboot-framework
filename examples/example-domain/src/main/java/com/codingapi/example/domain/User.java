package com.codingapi.example.domain;

import com.codingapi.springboot.flow.operator.IFlowOperator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User implements IFlowOperator {

    private long id;

    private String name;

    private String username;
    private String password;
}
