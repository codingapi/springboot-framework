package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowBackup;

/**
 *  流程备份仓库（流程快照仓库）
 */
public interface FlowBackupRepository {

    /**
     * 备份流程
     * @param flowWork 流程
     * @return 备份对象
     */
    FlowBackup backup(FlowWork flowWork);

    /**
     * 根据流程id和版本号获取备份
     * @param workId 流程id
     * @param workVersion 版本号
     * @return 备份对象
     */
    FlowBackup getFlowBackupByWorkIdAndVersion(long workId,long workVersion);

    /**
     * 根据备份id获取备份
     * @param backupId 备份id
     * @return 备份对象
     */
    FlowBackup getFlowBackupById(long backupId);

}
