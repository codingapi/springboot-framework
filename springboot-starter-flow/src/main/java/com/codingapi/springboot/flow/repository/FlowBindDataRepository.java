package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;

public interface FlowBindDataRepository {

    /**
     * 保存数据
     * @param snapshot 数据
     */
    void save(BindDataSnapshot snapshot);

}
