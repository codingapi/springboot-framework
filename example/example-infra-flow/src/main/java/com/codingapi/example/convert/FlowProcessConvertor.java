package com.codingapi.example.convert;

import com.codingapi.example.entity.FlowProcessEntity;
import com.codingapi.springboot.flow.record.FlowProcess;

public class FlowProcessConvertor {

    public static FlowProcess convert(FlowProcessEntity entity){
        if(entity==null){
            return null;
        }
        return new FlowProcess(entity.getProcessId(),
                entity.getCreateTime(),entity.getBackupId(),entity.getCreateOperatorId());
    }


    public static FlowProcessEntity convert(FlowProcess flowProcess){
        if(flowProcess==null){
            return null;
        }
        FlowProcessEntity entity = new FlowProcessEntity();
        entity.setProcessId(flowProcess.getProcessId());
        entity.setCreateTime(flowProcess.getCreateTime());
        entity.setBackupId(flowProcess.getBackupId());
        entity.setCreateOperatorId(flowProcess.getCreateOperatorId());
        return entity;
    }
}
