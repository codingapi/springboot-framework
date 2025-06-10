package com.codingapi.springboot.flow.bind;

import com.alibaba.fastjson.JSONObject;

/**
 * 数据绑定接口
 */
public interface IBindData {

    String CLASS_NAME_KEY = "clazzName";

    /**
     * 数据快照
     *
     * @return 数据快照
     */
    default String toJsonSnapshot() {
        return JSONObject.toJSONString(this);
    }


    /**
     * 获取类名称
     *
     * @return 类名称
     */
    default String getClazzName() {
        return this.getClass().getName();
    }


    /**
     * 类对象匹配
     */
    default boolean match(String dataKey) {
        String className = this.getClazzName();
        return dataKey.equals(className);
    }


    /**
     * 转化为类对象
     */
    default <T> T toJavaObject(Class<T> clazz) {
        return (T) this;
    }
}
