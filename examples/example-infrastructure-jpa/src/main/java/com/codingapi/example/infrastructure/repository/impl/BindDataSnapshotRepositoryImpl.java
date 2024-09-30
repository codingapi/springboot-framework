package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.infrastructure.convert.BindDataSnapshotConvertor;
import com.codingapi.example.infrastructure.entity.BindDataSnapshotEntity;
import com.codingapi.example.infrastructure.jpa.BindDataSnapshotEntityRepository;
import com.codingapi.springboot.flow.data.BindDataSnapshot;
import com.codingapi.springboot.flow.repository.BindDataSnapshotRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class BindDataSnapshotRepositoryImpl implements BindDataSnapshotRepository {

    private final BindDataSnapshotEntityRepository bindDataSnapshotEntityRepository;

    @Override
    public void save(BindDataSnapshot snapshot) {
        BindDataSnapshotEntity entity = BindDataSnapshotConvertor.convert(snapshot);
        entity = bindDataSnapshotEntityRepository.save(entity);
        snapshot.setId(entity.getId());
    }
}
