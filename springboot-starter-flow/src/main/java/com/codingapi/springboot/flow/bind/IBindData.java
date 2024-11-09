package com.codingapi.springboot.flow.bind;

import com.alibaba.fastjson.JSONObject;

/**
 * 数据绑定接口
 */
public interface IBindData {

    /**
     * 数据快照
     *
     * @return 数据快照
     */
    default String toJsonSnapshot() {
        return JSONObject.toJSONString(this);
    }
}
