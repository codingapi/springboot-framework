package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.serializable.FlowWorkSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 流程备份
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlowBackup {

    /**
     * 备份id
     */
    private long id;

    /**
     * 流程的字节码
     */
    private byte[] bytes;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 流程的版本号
     * 以流程的修改时间为准
     */
    private long workVersion;

    /**
     * 流程的设计id
     */
    private long workId;

    /**
     * 恢复流程
     * @param flowOperatorRepository 操作者仓库
     * @return 流程
     */
    public FlowWork resume(FlowOperatorRepository flowOperatorRepository) {
        return FlowWorkSerializable.fromSerializable(this.bytes).toFlowWork(flowOperatorRepository);
    }

    public FlowBackup(FlowWork flowWork) {
        this.bytes = flowWork.toSerializable().toSerializable();
        this.workVersion = flowWork.getUpdateTime();
        this.workId = flowWork.getId();
        this.createTime = System.currentTimeMillis();
    }
}
