package com.codingapi.example.infra.flow.repository;

import com.codingapi.example.infra.flow.convert.BindDataSnapshotConvertor;
import com.codingapi.example.infra.flow.entity.BindDataSnapshotEntity;
import com.codingapi.example.infra.flow.jpa.BindDataSnapshotEntityRepository;
import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FlowBindDataRepositoryImpl implements FlowBindDataRepository {

    private final BindDataSnapshotEntityRepository bindDataSnapshotEntityRepository;

    @Override
    public void save(BindDataSnapshot snapshot) {
        BindDataSnapshotEntity entity = BindDataSnapshotConvertor.convert(snapshot);
        entity = bindDataSnapshotEntityRepository.save(entity);
        snapshot.setId(entity.getId());
    }

    @Override
    public void update(BindDataSnapshot snapshot) {
        bindDataSnapshotEntityRepository.save(BindDataSnapshotConvertor.convert(snapshot));
    }

    @Override
    public BindDataSnapshot getBindDataSnapshotById(long id) {
        return BindDataSnapshotConvertor.convert(bindDataSnapshotEntityRepository.getBindDataSnapshotEntityById(id));
    }
}
