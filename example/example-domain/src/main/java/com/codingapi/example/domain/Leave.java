package com.codingapi.example.domain;

import com.codingapi.springboot.flow.bind.IBindData;

public class Leave implements IBindData {

    private long id;
    private String desc;
    private int days;

    @Override
    public long getId() {
        return id;
    }
}
