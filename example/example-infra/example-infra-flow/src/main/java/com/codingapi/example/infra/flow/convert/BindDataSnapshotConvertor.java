package com.codingapi.example.infra.flow.convert;

import com.codingapi.example.infra.flow.entity.BindDataSnapshotEntity;
import com.codingapi.springboot.flow.bind.BindDataSnapshot;

public class BindDataSnapshotConvertor {

    public static BindDataSnapshot convert(BindDataSnapshotEntity entity){
        if(entity==null){
            return null;
        }

        return new BindDataSnapshot(entity.getId(),entity.getSnapshot(),entity.getCreateTime(), entity.getClazzName());
    }


    public static BindDataSnapshotEntity convert(BindDataSnapshot snapshot){
        if(snapshot==null){
            return null;
        }

        BindDataSnapshotEntity entity = new BindDataSnapshotEntity();
        entity.setId(snapshot.getId());
        entity.setSnapshot(snapshot.getSnapshot());
        entity.setCreateTime(snapshot.getCreateTime());
        entity.setClazzName(snapshot.getClazzName());
        return entity;
    }
}
