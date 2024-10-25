package com.codingapi.example.repository;

import com.codingapi.example.convert.BindDataSnapshotConvertor;
import com.codingapi.example.jpa.BindDataSnapshotEntityRepository;
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
        bindDataSnapshotEntityRepository.save(BindDataSnapshotConvertor.convert(snapshot));
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
