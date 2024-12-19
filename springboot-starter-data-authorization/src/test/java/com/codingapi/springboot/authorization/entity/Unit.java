package com.codingapi.springboot.authorization.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "t_unit")
@NoArgsConstructor
public class Unit {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private long parentId;

    public Unit(String name, long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public Unit(String name) {
        this(name,0);
    }
}
