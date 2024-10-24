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
    private long id;

    private String title;

    private String description;

    private long createUser;

    private long createTime;

    private long updateTime;

    private boolean enable;

    private int postponedMax;

    @Lob
    private String schema;

}
