package com.codingapi.springboot.authorization.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "t_depart")
@NoArgsConstructor
public class Depart {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private long parentId;

    private long unitId;

    public Depart(String name, long unitId,long parentId) {
        this.name = name;
        this.parentId = parentId;
        this.unitId = unitId;
    }

    public Depart(String name,long unitId) {
        this(name,unitId,0);
    }
}
