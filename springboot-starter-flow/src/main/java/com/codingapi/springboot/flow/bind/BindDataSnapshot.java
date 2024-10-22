package com.codingapi.springboot.flow.bind;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

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


    @SneakyThrows
    public IBindData toBindData(){
        return JSONObject.parseObject(snapshot,(Class<? extends IBindData>) Class.forName(clazzName));
    }
}
