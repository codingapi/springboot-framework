package com.codingapi.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 考虑到数据量大的情况下可以根据clazzName分表存储
 */
@Setter
@Getter
@Entity
public class BindDataSnapshotEntity {
    /**
     * 数据快照id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * 快照信息
     */
    @Lob
    private String snapshot;
    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 数据绑定类名称
     */
    private String clazzName;


}
