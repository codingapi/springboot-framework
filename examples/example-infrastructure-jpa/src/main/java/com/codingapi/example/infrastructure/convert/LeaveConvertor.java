package com.codingapi.example.infrastructure.convert;

import com.codingapi.example.domain.Leave;
import com.codingapi.example.infrastructure.entity.LeaveEntity;
import com.codingapi.springboot.fast.manager.EntityManagerContent;

public class LeaveConvertor {

    public static Leave convert(LeaveEntity entity){
        if(entity==null){
            return null;
        }

        Leave leave = new Leave();
        leave.setId(entity.getId());
        leave.setCreateTime(entity.getCreateTime());
        leave.setDesc(entity.getDesc());
        leave.setStartDate(entity.getStartDate());
        leave.setEndDate(entity.getEndDate());
        leave.setUser(UserConvertor.convert(entity.getUser()));

        EntityManagerContent.getInstance().detach(entity);
        return leave;
    }

    public static LeaveEntity convert(Leave leave){
        if(leave==null){
            return null;
        }

        LeaveEntity entity = new LeaveEntity();
        entity.setId(leave.getId());
        entity.setCreateTime(leave.getCreateTime());
        entity.setDesc(leave.getDesc());
        entity.setStartDate(leave.getStartDate());
        entity.setEndDate(leave.getEndDate());
        entity.setUser(UserConvertor.convert(leave.getUser()));
        return entity;
    }
}
