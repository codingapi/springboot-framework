package com.codingapi.springboot.example.infrastructure.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "t_demo")
public class DemoEntity {

    @Id
    private Integer id;

    private String name;


}
