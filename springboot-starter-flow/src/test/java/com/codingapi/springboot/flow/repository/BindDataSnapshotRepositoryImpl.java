package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.data.BindDataSnapshot;
import com.codingapi.springboot.flow.repository.BindDataSnapshotRepository;

import java.util.ArrayList;
import java.util.List;

public class BindDataSnapshotRepositoryImpl implements BindDataSnapshotRepository {

    private final List<BindDataSnapshot> snapshots = new ArrayList<>();

    @Override
    public void save(BindDataSnapshot snapshot) {
        if(snapshot.getId()==0){
            snapshots.add(snapshot);
            snapshot.setId(snapshots.size());
        }

    }
}
