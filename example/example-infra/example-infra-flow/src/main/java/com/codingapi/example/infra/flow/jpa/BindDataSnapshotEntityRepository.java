package com.codingapi.example.infra.flow.jpa;

import com.codingapi.example.infra.flow.entity.BindDataSnapshotEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface BindDataSnapshotEntityRepository extends FastRepository<BindDataSnapshotEntity,Long> {


    BindDataSnapshotEntity getBindDataSnapshotEntityById(long id);


}
