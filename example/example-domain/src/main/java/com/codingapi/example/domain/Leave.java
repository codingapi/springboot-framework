package com.codingapi.example.domain;

import com.codingapi.springboot.flow.bind.IBindData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Leave implements IBindData {

    private long id;
    private String desc;
    private int days;
    private String username;
    private long createTime;


}
