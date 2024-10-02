package com.codingapi.example.infrastructure.entity.flow;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FlowWorkEntity {

    /**
     * 流程的设计id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * 流程标题
     */
    private String title;
    /**
     * 流程描述
     */
    @Lob
    private String description;
    /**
     * 流程创建者
     */
    private long createUserId;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 是否启用
     */
    private boolean enable;
    /**
     * 是否锁定
     * 锁定流程将无法发起新的流程，当前存在的流程不受影响
     */
    private boolean lock;
    /**
     * 流程的节点(发起节点)
     */
    private String nodeIds;
    /**
     * 界面设计脚本
     */
    @Lob
    private String schema;
}
