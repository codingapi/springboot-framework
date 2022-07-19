package com.codingapi.springboot.framework.serializable;


import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public interface JsonSerializable extends Serializable {

    default String toJson() {
        return JSONObject.toJSONString(this);
    }

}
