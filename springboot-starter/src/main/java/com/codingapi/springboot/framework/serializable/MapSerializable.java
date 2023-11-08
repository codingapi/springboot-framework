package com.codingapi.springboot.framework.serializable;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;

import java.util.Map;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface MapSerializable {

    default Map<String, Object> toMap() {
        return JSON.parseObject(JSONObject.toJSONString(this));
    }
}
