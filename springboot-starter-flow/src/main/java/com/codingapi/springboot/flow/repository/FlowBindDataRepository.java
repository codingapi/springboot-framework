package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;

public interface FlowBindDataRepository {

    /**
     * 保存数据
     * @param bindData 数据
     * @return 数据快照
     */
    BindDataSnapshot save(IBindData bindData);
}
