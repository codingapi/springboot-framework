package com.codingapi.example.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class LeaveEntity {

    /**
     * 请假ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 请假原因
     */
    private String desc;

    /**
     * 请假用户
     */
    @ManyToOne
    private UserEntity user;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;
}
