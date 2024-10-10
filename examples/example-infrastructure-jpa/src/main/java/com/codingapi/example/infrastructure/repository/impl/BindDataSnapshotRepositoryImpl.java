package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.infrastructure.convert.BindDataSnapshotConvertor;
import com.codingapi.example.infrastructure.entity.flow.BindDataSnapshotEntity;
import com.codingapi.example.infrastructure.jpa.BindDataSnapshotEntityRepository;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.data.BindDataSnapshot;
import com.codingapi.springboot.flow.repository.BindDataSnapshotRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BindDataSnapshotRepositoryImpl implements BindDataSnapshotRepository {

    private final BindDataSnapshotEntityRepository bindDataSnapshotEntityRepository;

    public BindDataSnapshotRepositoryImpl(BindDataSnapshotEntityRepository bindDataSnapshotEntityRepository) {
        this.bindDataSnapshotEntityRepository = bindDataSnapshotEntityRepository;

        FlowRepositoryContext.getInstance().bind(this);
    }

    @Override
    public void save(BindDataSnapshot snapshot) {
        BindDataSnapshotEntity entity = BindDataSnapshotConvertor.convert(snapshot);
        entity = bindDataSnapshotEntityRepository.save(entity);
        snapshot.setId(entity.getId());
    }

    @Override
    public BindDataSnapshot getBindDataSnapshotById(long id) {
        return BindDataSnapshotConvertor.convert(bindDataSnapshotEntityRepository.getBindDataSnapshotEntityById(id));
    }
}