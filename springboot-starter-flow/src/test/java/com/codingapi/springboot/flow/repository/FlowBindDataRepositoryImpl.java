package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FlowBindDataRepositoryImpl implements FlowBindDataRepository {

    private final List<BindDataSnapshot> cache = new ArrayList<>();

    @Override
    public void save(BindDataSnapshot snapshot) {
        if (snapshot.getId() == 0) {
            cache.add(snapshot);
            snapshot.setId(cache.size());
        }
    }

    @Override
    public void update(BindDataSnapshot snapshot) {
        BindDataSnapshot old = getBindDataSnapshotById(snapshot.getId());
        if (old != null) {
           old.setSnapshot(snapshot.getSnapshot());
        }
    }

    @Override
    public BindDataSnapshot getBindDataSnapshotById(long id) {
        return cache.stream().filter(snapshot -> snapshot.getId() == id).findFirst().orElse(null);
    }

    public List<BindDataSnapshot> findAll(){
        return cache;
    }
}
