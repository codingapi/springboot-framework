package com.codingapi.example.jpa;

import com.codingapi.example.entity.BindDataSnapshotEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface BindDataSnapshotEntityRepository extends FastRepository<BindDataSnapshotEntity,Long> {


    BindDataSnapshotEntity getBindDataSnapshotEntityById(long id);


}
