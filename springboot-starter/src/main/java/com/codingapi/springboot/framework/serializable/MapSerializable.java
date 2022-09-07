package com.codingapi.springboot.framework.serializable;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface MapSerializable {

    default Map<String, Object> toMap() {
        return (Map<String, Object>) JSONObject.toJSON(this);
    }
}
