package com.codingapi.example.infra.flow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FlowRelationEntity {

    /**
     * 关系id
     */
    @Id
    private String id;


    private Long workId;

    /**
     * 名称
     */
    private String name;

    /**
     * 源节点
     */
    private String sourceId;

    /**
     * 目标节点
     */
    private String targetId;

    /**
     * 排序
     */
    @Column(name = "s_order")
    private Integer order;

    /**
     * 是否退回
     */
    @Column(name = "s_back")
    private Boolean back;

    /**
     * 出口触发器
     */
    @Lob
    private String outTrigger;


    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

}
