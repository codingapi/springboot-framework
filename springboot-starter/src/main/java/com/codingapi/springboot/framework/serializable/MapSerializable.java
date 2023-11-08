package com.codingapi.springboot.framework.serializable;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface MapSerializable {

    default Map<String, Object> toMap() {
        return JSON.parseObject(JSON.toJSONString(this));
    }
}
