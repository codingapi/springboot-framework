package com.codingapi.example.convert;

import com.codingapi.example.entity.FlowWorkEntity;
import com.codingapi.springboot.flow.domain.FlowWork;

public class FlowWorkConvertor {


    public static FlowWorkEntity convert(FlowWork flowWork) {
        if (flowWork == null) {
            return null;
        }
        FlowWorkEntity entity = new FlowWorkEntity();
        entity.setId(flowWork.getId());
        entity.setTitle(flowWork.getTitle());
        entity.setDescription(flowWork.getDescription());
        entity.setCreateUser(flowWork.getCreateUser().getUserId());
        entity.setCreateTime(flowWork.getCreateTime());
        entity.setUpdateTime(flowWork.getUpdateTime());
        entity.setEnable(flowWork.isEnable());
        entity.setPostponedMax(flowWork.getPostponedMax());
        entity.setSchema(flowWork.getSchema());
        return entity;
    }
}
