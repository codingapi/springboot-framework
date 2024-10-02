package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.data.BindDataSnapshot;

public interface BindDataSnapshotRepository {

    void save(BindDataSnapshot snapshot);

    BindDataSnapshot getBindDataSnapshotById(long id);

}
