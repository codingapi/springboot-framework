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

}
