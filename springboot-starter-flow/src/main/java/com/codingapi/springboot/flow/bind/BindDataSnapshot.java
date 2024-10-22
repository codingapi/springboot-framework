package com.codingapi.springboot.flow.bind;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据快照
 */
@Setter
@Getter
public class BindDataSnapshot {

    /**
     * 数据快照id
     */
    private long id;
    /**
     * 快照信息
     */
    private String snapshot;
    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 数据绑定类名称
     */
    private String clazzName;


    public IBindData toBindData(){
        try {
            return JSONObject.parseObject(snapshot, (Class<? extends IBindData>) Class.forName(clazzName));
        }catch (Exception e){
            throw new IllegalArgumentException("bind data error");
        }
    }
}
