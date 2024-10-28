package com.codingapi.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FlowBackupEntity {

    /**
     * 备份id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 流程的字节码
     */
    @Lob
    private byte[] bytes;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 流程的版本号
     * 以流程的修改时间为准
     */
    private Long workVersion;

    /**
     * 流程的设计id
     */
    private Long workId;
}
