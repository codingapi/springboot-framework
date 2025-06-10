package com.codingapi.springboot.flow.bind;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * 流程绑定Map数据对象，用于分布式服务下的流程对象数据传递能力
 * 该对象中，将clazzName 当做了普通的key来使用，
 */
public class FlowMapBindData extends HashMap<String, Object> implements IBindData {


    /**
     * 获取类名称
     *
     * @return 类名称
     */
    @Override
    public String getClazzName() {
        return (String) this.get(CLASS_NAME_KEY);
    }

    /**
     * 转化为类对象
     */
    @Override
    public <T> T toJavaObject(Class<T> clazz) {
        return JSONObject.parseObject(toJsonSnapshot(), clazz);
    }

    public static FlowMapBindData fromJson(String json) {
       return JSONObject.parseObject(json, FlowMapBindData.class);
    }

    public static FlowMapBindData fromObject(Object obj) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj), FlowMapBindData.class);
    }

    public static FlowMapBindData fromJson(JSONObject json) {
        return JSONObject.parseObject(json.toJSONString(), FlowMapBindData.class);
    }

    public boolean match(String matchKey) {
        String className = this.getClazzName();
        return matchKey.equals(className);
    }
}
