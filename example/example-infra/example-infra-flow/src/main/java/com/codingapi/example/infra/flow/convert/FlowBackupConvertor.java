package com.codingapi.example.infra.flow.convert;

import com.codingapi.example.infra.flow.entity.FlowBackupEntity;
import com.codingapi.springboot.flow.record.FlowBackup;

public class FlowBackupConvertor {

    public static FlowBackup convert(FlowBackupEntity entity){
        if(entity==null){
            return null;
        }


        FlowBackup flowBackup = new FlowBackup();
        flowBackup.setId(entity.getId());
        flowBackup.setBytes(entity.getBytes());
        flowBackup.setCreateTime(entity.getCreateTime());
        flowBackup.setWorkVersion(entity.getWorkVersion());
        flowBackup.setWorkId(entity.getWorkId());
        return flowBackup;
    }


    public static FlowBackupEntity convert(FlowBackup flowBackup){
        if(flowBackup==null){
            return null;
        }

        FlowBackupEntity entity = new FlowBackupEntity();
        entity.setId(flowBackup.getId());
        entity.setBytes(flowBackup.getBytes());
        entity.setCreateTime(flowBackup.getCreateTime());
        entity.setWorkVersion(flowBackup.getWorkVersion());
        entity.setWorkId(flowBackup.getWorkId());
        return entity;
    }
}
