package com.codingapi.springboot.leaf.common;


import com.codingapi.springboot.leaf.IDGen;

public class ZeroIDGen implements IDGen {

    @Override
    public Result get(String key) {
        return new Result(0, Status.SUCCESS);
    }

    @Override
    public boolean init() {
        return true;
    }
}
