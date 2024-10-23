package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;

public interface FlowBindDataRepository {

    /**
     * 保存数据
     * @param snapshot 数据
     */
    void save(BindDataSnapshot snapshot);

    /**
     * 更新数据
     * @param snapshot 数据
     */
    void update(BindDataSnapshot snapshot);


    /**
     * 查询快照数据
     * @param id 快照id
     * @return BindDataSnapshot
     */
    BindDataSnapshot getBindDataSnapshotById(long id);

}
