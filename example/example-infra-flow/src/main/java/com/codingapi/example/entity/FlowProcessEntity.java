package com.codingapi.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FlowProcessEntity {

    /**
     * 流程id
     */

    @Id
    private String processId;

    /**
     * 流程的字节码
     */
    private Long backupId;


    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建者id
     */
    private Long createOperatorId;

}
