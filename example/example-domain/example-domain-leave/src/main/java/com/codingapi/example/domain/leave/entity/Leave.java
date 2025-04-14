package com.codingapi.example.domain.leave.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Leave {

    private long id;
    private String desc;
    private int days;
    private String username;
    private long createTime;

}
