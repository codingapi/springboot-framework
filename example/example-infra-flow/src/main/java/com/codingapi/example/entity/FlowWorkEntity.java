package com.codingapi.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FlowWorkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String title;

    private String description;

    private Long createUser;

    private Long createTime;

    private Long updateTime;

    private Boolean enable;

    private Integer postponedMax;

    @Lob
    @Column(name = "m_schema", columnDefinition = "longtext")
    private String schema;

}
