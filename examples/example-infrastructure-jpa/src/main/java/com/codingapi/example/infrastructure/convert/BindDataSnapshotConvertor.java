package com.codingapi.example.infrastructure.convert;

import com.codingapi.example.infrastructure.entity.flow.BindDataSnapshotEntity;
import com.codingapi.springboot.fast.manager.EntityManagerContent;
import com.codingapi.springboot.flow.data.BindDataSnapshot;

public class BindDataSnapshotConvertor {

    public static BindDataSnapshot convert(BindDataSnapshotEntity entity){
        if(entity==null){
            return null;
        }
        BindDataSnapshot snapshot = new BindDataSnapshot();
        snapshot.setId(entity.getId());
        snapshot.setSnapshot(entity.getSnapshot());
        snapshot.setCreateTime(entity.getCreateTime());
        snapshot.setClazzName(entity.getClazzName());

        EntityManagerContent.getInstance().detach(entity);
        return snapshot;
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
