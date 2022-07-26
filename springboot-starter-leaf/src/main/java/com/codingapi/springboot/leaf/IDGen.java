package com.codingapi.springboot.leaf;


import com.codingapi.springboot.leaf.common.Result;

public interface IDGen {

    Result get(String key);

    boolean init();

}
