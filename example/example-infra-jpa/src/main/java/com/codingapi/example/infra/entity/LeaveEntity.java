package com.codingapi.example.infra.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class LeaveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "m_desc")
    private String desc;
    private int days;
    private String username;
    private long createTime;
}
