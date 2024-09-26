package com.codingapi.springboot.flow.data;

import com.alibaba.fastjson.JSONObject;

/**
 * 流程数据的绑定对象
 * 只要实现这个接口，就可以作为流程的数据对象
 */
public interface IBindData {

    /**
     * 获取数据ID
     *
     * @return 数据ID
     */
    long getId();

    /**
     * 数据快照
     *
     * @return 数据快照
     */
    default String toJsonSnapshot() {
        return JSONObject.toJSONString(this);
    }

}
